package hkh.nlp.earley;

import java.util.ArrayList;
import java.util.List;

public class NonTerminalRules {

	public ArrayList<NTRule> ntrules = null;
	public NonTerminalRules() {
		ntrules = new ArrayList<NTRule>();
	}
	
	/**
	 * grammar rule 추가
	 * @param elements
	 */
	public void addRule(String raw) {
		ntrules.add(NTRule.makeRule(raw));
	}
	
	/**
	 * PREDICTOR 메서드, 해당 state non-terminal string에 해당하는 모든 Rule을
	 * State List형식으로 반환
	 * @param state
	 * @return
	 */
	public List<State> grammarRulesFor(State state) {
		List<State> matched = new ArrayList<State>();
		String next = state.getNextString();
		for (NTRule ntrule : ntrules) {
			if (ntrule.lhs.equals(next)) {
				// matched, create new State
				ArrayList<String[]> completed = new ArrayList<String[]>();
				String[] elm = {};
				completed.add(elm);
				String[] incompleted = ntrule.rhs.toArray(new String[0]);
				State newState = new State(state.current, state.current, next, completed, incompleted);
				newState.operation = "Predictor";
				matched.add(newState);
			}
		}
		return matched;
	}
	
	static class NTRule {
		
		public String lhs = null;
		public ArrayList<String> rhs = null;
		
		private NTRule() {
			rhs = new ArrayList<String>();
		}
		
		public static NTRule makeRule(String raw) {
			NTRule rule = new NTRule();
			String[] elems = raw.split("\\s");
			try {
				rule.lhs = elems[0];
				for (int i=2; i<elems.length; i++) {
					rule.rhs.add(elems[i]);
				}
			} catch (RuntimeException re) {
				System.err.println("malformed grammar file !");
				System.exit(0);
			}
			return rule;
		}
		
		@Override
		public String toString() {
			StringBuffer ret = new StringBuffer();
			ret.append(lhs);
			ret.append(" >> ");
			for (String s : rhs) {
				ret.append(s+" ");
			}
			return ret.toString();
		}
	}
	
	public void printRules() {
		System.out.println("-- Non Terminal Rules --");
		for (NTRule rule : ntrules) {
			System.out.println(rule.toString());
		}
	}
}
