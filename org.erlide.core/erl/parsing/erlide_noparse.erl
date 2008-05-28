%% Author: jakob
%% Created: Mar 23, 2006
%% Description: TODO: Add description to erlide_open

-module(erlide_noparse).

%%
%% Exported Functions
%%

-export([initial_parse/4, reparse/1]). 

%%
%% Include files
%%

-define(DEBUG, 1).

-define(CACHE_VERSION, 2).

-include("erlide.hrl").
-include("erlide_scanner.hrl").

-record(function, {pos, name, arity, args, head, clauses, name_pos}).
-record(clause, {pos, name, args, head, code, name_pos}).
-record(attribute, {pos, name, args}).
-record(other, {pos, name, tokens}).

initial_parse(ScannerName, ModuleFileName, InitialText, StateDir) ->
    try
    	?D({StateDir, ModuleFileName}),
		RenewFun = fun(_F) -> do_parse(ScannerName, ModuleFileName, InitialText, StateDir) end,
        CacheFun = fun(D) -> erlide_scanner2:initialScan(ScannerName, ModuleFileName, InitialText, StateDir), D end,
    	CacheFileName = filename:join(StateDir, atom_to_list(ScannerName) ++ ".noparse"),
        ?D(CacheFileName),
		Res = erlide_util:check_cached(ModuleFileName, CacheFileName, ?CACHE_VERSION, RenewFun, CacheFun),
        {ok, Res}
    catch
        error:Reason ->
            {error, Reason}
    end.

reparse(ScannerName) ->
    try
        Res = do_parse(ScannerName, "", "", ""),
        {ok, Res}
    catch
        error:Reason ->
            {error, Reason}
    end.

do_parse(ScannerName, ModuleFileName, InitalText, StateDir) ->
    ?Info({noparse, ScannerName}),
    Toks = scan(ScannerName, ModuleFileName, InitalText, StateDir),
    ?D(length(Toks)),
    {UncommentToks, Comments} = extract_comments(Toks),
    ?D({length(UncommentToks), length(Comments)}),
    Functions = split_after_dots(UncommentToks, [], []),
    ?D(length(Functions)),
    Collected = [classify_and_collect(I) || I <- Functions, I =/= [eof]],
    ?D(length(Collected)),
    {Collected, Comments, Toks}.

classify_and_collect(C) ->
    ?D(C),
    R = cac(check_class(C), C),
    ?D(R),
    R.

cac(function, Tokens) ->
    ClauseList = split_clauses(Tokens),
	?D(ClauseList),
    Clauses = [fix_clause(C) || C <- ClauseList],
	?D(length(Clauses)),
    [#clause{pos=P, name=N, args=A, head=H, name_pos=NP} | _] = Clauses,
    Arity = erlide_text:guess_arity(A),
	?D(Arity),
    case Clauses of					% only show subclauses when more than one
        [_] ->
            #function{pos=P, name=N, arity=Arity, args=A, head=H, clauses=[], name_pos=NP};
        _ ->
            #function{pos=P, name=N, arity=Arity, clauses=Clauses, name_pos=NP}
    end;
cac(attribute, Attribute) ->
    case Attribute of
        [#token{kind='-', offset=Offset, line=Line}, 
         #token{kind=atom, value=Name, line=_Line, offset=_Offset},
         _, #token{value=Args} | _] = Attribute ->
            #token{line=LastLine, offset=LastOffset, 
                   length=LastLength} = last_not_eof(Attribute),
            PosLength = LastOffset - Offset + LastLength,
            #attribute{pos={{Line, LastLine, Offset}, PosLength},
                       name=Name, args=Args};
        [_, #token{kind=atom, value=Name, line=Line, offset=Offset} | _] ->
            #token{line=LastLine, offset=LastOffset, 
                   length=LastLength} = last_not_eof(Attribute),
            PosLength = LastOffset - Offset + LastLength,
            #attribute{pos={{Line, LastLine, Offset}, PosLength},
                       name=Name, args=[]}
    end;
cac(other, [#token{value=Name, line=Line,
                   offset=Offset, length=Length} | _]) ->
    #other{pos={{Line, Line, Offset}, Length}, name=Name};
cac(_, _D) ->
	?D(_D),
	eof.

check_class([#token{kind = atom}, #token{kind = '('} | _]) ->
    function;
check_class([#token{kind = '-'}, #token{kind = atom} | _]) ->
    attribute;
check_class(_) ->
	?D(ok),
    other.

to_string(Tokens) ->
    erlide_scanner2:tokens_to_string(Tokens).

get_head(T) ->
    case get_args(T) of
        "" ->
            "";
        A ->
            case get_guards(T) of
                "" ->
                    A;
                G ->
                    A ++ " when " ++ G
            end
    end.

get_args(T) ->
    case get_between_pars(T) of
        "" ->
            "";
        P ->
            "("++to_string(P)++")"
    end.


get_guards(T) ->
    to_string(get_between(T, 'when', '->')). 

get_between_pars(T) ->
    get_between(T, '(', ')').

last_not_eof(L) -> 
    case lists:reverse(L) of
        [eof, Last | _] ->
            Last;
        [Last | _] ->
            Last;
        _ ->
            error
    end.    


get_between(L, A, B) ->
    R = get_upto(L, B),
    lists:reverse(get_upto(lists:reverse(R), A)).

get_upto(L, Delim) ->
	get_upto(L, Delim, []).

get_upto([], _Delim, _Acc) ->
    [];
get_upto([#token{kind=Delim} | _], Delim, Acc) ->
	lists:reverse(Acc);
get_upto([T | Rest], Delim, Acc) ->
    get_upto(Rest, Delim, [T | Acc]).

%% get_between([], _A, _B) ->
%%     [];
%% get_between([#token{kind = A} | Rest], A, B) ->
%%     get_between2(Rest, B, []);
%% get_between([_ | Rest], A, B) ->
%%     get_between(Rest, A, B).
%% 
%% get_between2([], _B, Acc) ->
%%     lists:reverse(Acc);
%% get_between2([#token{kind = B} | _], B, Acc) ->
%%     get_between2([], B, Acc);
%% get_between2([T | Rest], B, Acc) ->
%%     get_between2(Rest, B, [T | Acc]).

reverse2(L) ->
    lists:reverse([lists:reverse(A) || A <- L]).

split_after_dots([], Acc, []) ->
    reverse2(Acc);
split_after_dots([], Acc, FunAcc) ->
    split_after_dots([], [FunAcc | Acc], []);
split_after_dots([#token{kind = eof} | _], Acc, FunAcc) ->
    split_after_dots([], Acc, FunAcc);
split_after_dots([T = #token{kind = dot} | TRest], Acc, FunAcc) ->
    split_after_dots(TRest, [[T | FunAcc] | Acc], []);
split_after_dots([T | TRest], Acc, FunAcc) ->
    split_after_dots(TRest, Acc, [T | FunAcc]).

check_clause([#token{kind = ';'} | Rest]) ->
    check_class(Rest) == function;
check_clause(_) ->
    false.

split_clauses(F) ->
    split_clauses(F, [], []).

split_clauses([], Acc, []) ->
    reverse2(Acc);
split_clauses([], Acc, ClAcc) ->
    split_clauses([], [ClAcc | Acc], []);
split_clauses([T | TRest] = Tokens, Acc, ClAcc) ->
    case check_clause(Tokens) of
        false ->
            split_clauses(TRest, Acc, [T | ClAcc]);
        true ->
            split_clauses(TRest, [[T | ClAcc] | Acc], [])
    end.

fix_clause([#token{kind=atom, value=Name, line=Line, offset=Offset, length=Length} | Rest]) ->
    #token{line=LastLine, offset=LastOffset, length=LastLength} = last_not_eof(Rest),
    PosLength = LastOffset - Offset + LastLength,
    #clause{pos={{Line, LastLine, Offset}, PosLength},
            name=Name, args=get_between_pars(Rest), head=get_head(Rest), code=[],
            name_pos={{Line, Offset}, Length}}.

scan(ScannerName, ModuleFileName, InitialText, StateDir) ->
    erlide_scanner2:initialScan(ScannerName, ModuleFileName, InitialText, StateDir),
    S = erlide_scanner2:getTokens(ScannerName),
    S.

%% @spec (Tokens::tokens()) -> {tokens(), tokens()}
%% @type tokens() = [#token]
%%
%% @doc extract comments from tokens, and concatenate multiline comments to one token

extract_comments(Tokens) ->
    extract_comments(Tokens, -1, [], []).

extract_comments([], _, TAcc, CAcc) ->
    {lists:reverse(TAcc), lists:reverse(CAcc)};
extract_comments([#token{kind=comment, offset=ONext, length=LNext, line=NNext,
                         value=VNext}
                  | Rest], NNext, TAcc,
                 [#token{kind=comment, offset=O, value=V}=C | CAcc]) ->
    NewComment = C#token{offset=O, length=ONext-O+LNext, value=V++"\n"++VNext,
                         last_line=NNext},
    extract_comments(Rest, NNext+1, TAcc, [NewComment | CAcc]);
extract_comments([C = #token{kind=comment, line=N} | Rest], _, TAcc, CAcc) ->
    extract_comments(Rest, N+1, TAcc, [C | CAcc]);
extract_comments([T | Rest], _, TAcc, CAcc) ->
    extract_comments(Rest, -1, [T | TAcc], CAcc).























