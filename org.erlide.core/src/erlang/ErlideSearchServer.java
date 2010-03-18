package erlang;

import java.util.ArrayList;
import java.util.List;

import org.erlide.core.erlang.util.ErlangFunction;
import org.erlide.core.search.ErlangExternalFunctionCallRef;
import org.erlide.core.search.ModuleLineFunctionArityRef;
import org.erlide.jinterface.backend.Backend;
import org.erlide.jinterface.backend.BackendException;
import org.erlide.jinterface.backend.util.Util;
import org.erlide.jinterface.util.ErlLogger;
import org.erlide.runtime.backend.ErlideBackend;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangRangeException;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class ErlideSearchServer {

	public static void addModules(final ErlideBackend backend,
			final List<String> modules) {
		try {
			backend.call(30000, "erlide_search_server", "add_modules", "ls",
					modules);
		} catch (final BackendException e) {
			ErlLogger.error(e); // TODO report error
		}

	}

	public static List<ModuleLineFunctionArityRef> functionUse(final Backend b,
			final FunctionRef ref) {
		return functionUse(b, ref.module, ref.function, ref.arity);
	}

	public static List<ModuleLineFunctionArityRef> macroOrRecordUse(
			final Backend b, final String macroOrRecord, final String name) {
		final List<ModuleLineFunctionArityRef> result = new ArrayList<ModuleLineFunctionArityRef>();
		try {
			final OtpErlangObject r = b.call("erlide_search_server",
					"find_refs", "aa", macroOrRecord, name);
			if (Util.isOk(r)) {
				addSearchResult(result, r);
			}
		} catch (final Exception e) {
			ErlLogger.error(e); // TODO report error
		}
		return result;
	}

	public static List<ModuleLineFunctionArityRef> includeUse(final Backend b,
			final String name) {
		final List<ModuleLineFunctionArityRef> result = new ArrayList<ModuleLineFunctionArityRef>();
		try {
			final OtpErlangObject r = b.call("erlide_search_server",
					"find_refs", "as", "include", name);
			if (Util.isOk(r)) {
				addSearchResult(result, r);
			}
		} catch (final Exception e) {
			ErlLogger.error(e); // TODO report error
		}
		return result;
	}

	public static List<ModuleLineFunctionArityRef> functionUse(final Backend b,
			final String mod, final String fun, final int arity) {
		final List<ModuleLineFunctionArityRef> result = new ArrayList<ModuleLineFunctionArityRef>();
		try {
			final OtpErlangObject r = b.call("erlide_search_server",
					"find_refs", "aai", mod, fun, arity);
			if (Util.isOk(r)) {
				addSearchResult(result, r);
			}
		} catch (final Exception e) {
			ErlLogger.error(e); // TODO report error
		}
		return result;
	}

	private static void addSearchResult(
			final List<ModuleLineFunctionArityRef> result,
			final OtpErlangObject r) throws OtpErlangRangeException {
		final OtpErlangTuple t = (OtpErlangTuple) r;
		final OtpErlangList l = (OtpErlangList) t.elementAt(1);
		for (final OtpErlangObject i : l) {
			final OtpErlangTuple modLineT = (OtpErlangTuple) i;
			final OtpErlangTuple modT = (OtpErlangTuple) modLineT.elementAt(0);
			final String modName = Util.stringValue(modT.elementAt(1));
			final OtpErlangLong lineL = (OtpErlangLong) modLineT.elementAt(1);
			final OtpErlangObject funOrAttr = modLineT.elementAt(2);
			ErlangFunction function;
			String attribute;
			if (funOrAttr instanceof OtpErlangTuple) {
				final OtpErlangTuple funT = (OtpErlangTuple) funOrAttr;
				final OtpErlangAtom funA = (OtpErlangAtom) funT.elementAt(0);
				final OtpErlangLong arL = (OtpErlangLong) funT.elementAt(1);
				function = new ErlangFunction(funA.atomValue(), arL.intValue());
				attribute = null;
			} else {
				function = null;
				attribute = Util.stringValue(funOrAttr);
			}
			final OtpErlangObject arg = modLineT.elementAt(3);
			final OtpErlangAtom subClause = (OtpErlangAtom) modLineT
					.elementAt(4);
			result.add(new ModuleLineFunctionArityRef(modName,
					lineL.intValue(), function, Util.stringValue(arg),
					subClause.atomValue().equals("true"), attribute));
		}
	}

	public static List<ModuleLineFunctionArityRef> functionUse(
			final ErlideBackend backend,
			final ErlangExternalFunctionCallRef searchRef) {
		return functionUse(backend, searchRef.getModule(), searchRef
				.getFunction(), searchRef.getArity());
	}

}
