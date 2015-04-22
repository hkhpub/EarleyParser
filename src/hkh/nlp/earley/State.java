package hkh.nlp.earley;

import java.util.ArrayList;
import java.util.List;

/**
 * Earley parser 알고리즘의 state를 나타내는 클래스다.
 * @author hkh
 *
 */
public class State {
	
	private static final String ROOT = "ROOT";
	
//	private static boolean DEBUG_PRINT = true;
	private static boolean DEBUG_PRINT = false;
	
	private static int uid = 0;
	
	public String id = null;
	
	/**
	 * 시작위치
	 */
	public int start = 0;
	
	/**
	 * 현재위치
	 */
	public int current = 0;
	
	/**
	 * left hand side of rule
	 */
	public String lhs = null;
	
	/**
	 * 발견된(completed) 규칙 right hand side
	 */
	public ArrayList<String[]> completed = null;
	
	/**
	 * 찾아야 하는 (in-completed) 규칙 right hand side
	 */
	public String[] incompleted = null;
	
	/**
	 * back pointer state
	 */
	public ArrayList<ArrayList<State>> backPointers = null;
	
	/**
	 * 생성된 operation (Seed, Predictor, Scanner, Completer)
	 */
	public String operation = null;
	
	public State(int start, int current, String lhs, ArrayList<String[]> completed, String[] incompleted) {
		this.start = start;
		this.current = current;
		this.lhs = lhs;
		this.completed = completed;
		this.incompleted = incompleted;
		this.backPointers = new ArrayList<ArrayList<State>>();
		ArrayList<State> bp = new ArrayList<State>();
		this.backPointers.add(bp);
	}
	
	public static State createDummyState() {
		ArrayList<String[]> completed = new ArrayList<String[]>();
		String[] elm = {};
		completed.add(elm);
		String[] incompleted = {"S"};
		State state = new State(0, 0, State.ROOT, completed, incompleted);
		state.operation = "Seed";
		return state;
	}
	
	/**
	 * state의 complete 상태 반환
	 * @return
	 */
	public boolean isComplete() {
		return incompleted.length == 0;
	}
	
	/**
	 * 찾고자 하는 string이 terminal이면 true, non-terminal false
	 * @return
	 */
	public boolean isNextPOS(TerminalRules trulesInst) {
		String next = getNextString();
		return trulesInst.isPOS(next);
	}
	
	/**
	 * 다음 파싱할 string 리턴
	 * @return
	 */
	public String getNextString() {
		if (incompleted.length <= 0) {
			return "";
		}
		return incompleted[0];
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String compstr = completedStr();
		String incompstr = incompletedStr();
		String bpstr = backPointerStr();
		if (DEBUG_PRINT) {
			sb.append(String.format("(%s) [%d, %d, [%s], [%s], [%s]]", id, start, current, lhs, compstr, incompstr));
			sb.append(" ("+operation+")");
			sb.append(" bp-> "+bpstr);
		} else {
			sb.append(String.format("[%d, %d, [%s], [%s], [%s]]", start, current, lhs, compstr, incompstr));
		}
		return sb.toString();
	}
	
	private String completedStr() {
		String compstr = "";
		for (String[] elm : completed) {
			compstr += "[";
			for (String s : elm) {
				compstr += s+" ";
			}
			compstr += "]";
		}
		
		// remove last space
		if (compstr.length()>0) {
			compstr=compstr.substring(0, compstr.length()-2);
			compstr += "]";
		}
		return compstr;
	}
	
	private String incompletedStr() {
		String incompstr = "";
		for (String s : incompleted) {
			incompstr += s+" ";
		}
		// remove last space.
		if (incompstr.length()>0) {
			incompstr=incompstr.substring(0, incompstr.length()-1);
		}
		return incompstr;
	}
	
	private String backPointerStr() {
		String bpstr = "";
		for (ArrayList<State> backPointer : backPointers) {
			bpstr += "[";
			for (State s : backPointer) {
				bpstr += s.id+" ";
			}
			bpstr += "]";
		}
		return bpstr;
	}
	
	private String bpListStrFormat() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (ArrayList<State> backPointer : backPointers) {
			sb.append("[");
			for (State s : backPointer) {
				sb.append(s.id+",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("],");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		
		return sb.toString();
	}
	
	/**
	 * 두 State가 같은지 판단. 같은 chart[i]에 중복으로 추가됨을 방지
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean sameState(State s1, State s2) {
		if (s1.start != s2.start || s1.current != s2.current 
				|| s1.incompleted.length != s2.incompleted.length
				|| s1.completed.size() != s1.completed.size()) {
			return false;
		}
		if (!s1.lhs.equals(s2.lhs)) {
			return false;
		}
		for (int i=0; i<s1.incompleted.length; i++) {
			if (!s1.incompleted[i].equals(s2.incompleted[i])) {
				return false;
			}
		}
		
		// completed 목록이 같은지 여부
		for (int i=0; i<s1.completed.size(); i++) {
			String[] elm1 = s1.completed.get(i);
			String[] elm2 = s2.completed.get(i);
			if (elm1.length != elm2.length) {
				return false;
			}
			for (int j=0; j<elm1.length; j++) {
				if (!elm1[j].equals(elm2[j])) {
					return false;
				}
			}
		}
		if (!State.isSameBackPointer(s1, s2)) {
			return false;
		}
//		System.out.println("Same state:\n"+s1.toString()+"\n"+s2.toString());
		return true;
	}
	
	public static boolean isSameBackPointer(State s1, State s2) {
		if (s1.backPointers.size() != s2.backPointers.size()) {
			return false;
		}
		for (int i=0; i<s1.backPointers.size(); i++) {
			ArrayList<State> bp1 = s1.backPointers.get(i);
			ArrayList<State> bp2 = s2.backPointers.get(i);
			if (bp1.size() != bp2.size()) {
				return false;
			}
			for (int j=0; j<bp1.size(); j++) {
				if (!bp1.get(j).id.equals(bp2.get(j).id)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static String getNewId() {
		String id = "s"+State.uid;
		State.uid++;
		return id;
	}
	
	/**
	 * 완전한 문장을 형성할 수 있는 최종 State 여부
	 * @param state
	 * @return
	 */
	public static boolean isFinalState(State state) {
		return state.lhs.equals(State.ROOT);
	}
	
	public static void getFinalStateResults(ArrayList<StringBuffer> bufferList, State state) {
		
		// 최종 packed tree list
		ArrayList<State> treeStates = new ArrayList<State>();
		
		StringBuffer sbuf = new StringBuffer();
		bufferList.add(sbuf);
		if (state.backPointers.size() > 0) {
			ArrayList<State> bp = state.backPointers.get(0);
			State s = bp.get(0);
			State.getBackPointerResults(treeStates, bufferList, sbuf, s);
			System.out.println();
		} else {
			System.out.println("N/A");
		}
		
		// print packed tree
		State.printPackedTree(treeStates);
	}
	
	private static void getBackPointerResults(ArrayList<State> treeStates, 
			ArrayList<StringBuffer> bufferList, StringBuffer sb, State state) {
		sb.append("("+state.lhs+" ");
		
		// make branch if state has multiple backPointers size > 1
		if (state.backPointers.size() > 1) {
			StringBuffer sbufNew = new StringBuffer();
			sbufNew.append(sb);
			bufferList.add(sbufNew);
			
			ArrayList<State> bp = state.backPointers.get(1);
			for (int j=0; j<bp.size(); j++) {
				State s = bp.get(j);
				State.getBackPointerResults(treeStates, bufferList, sbufNew, s);
				sbufNew.append(")");
			}
		}

		ArrayList<State> bp = state.backPointers.get(0);
		if (bp.size() == 0) {
			sb.append(state.completed.get(0)[0]);
		}
		for (int j=0; j<bp.size(); j++) {
			State s = bp.get(j);
			State.getBackPointerResults(treeStates, bufferList, sb, s);
		}
		sb.append(")");
		
		treeStates.add(state);
	}
	
	/**
	 * packed tree 출력
	 * @param treeStates
	 */
	private static void printPackedTree(ArrayList<State> treeStates) {
		System.out.println("-- Packed Tree Indexer --");
		for (int i=0; i<treeStates.size(); i++) {
			State state = treeStates.get(i);
			state.id = String.valueOf(i);
			State.printTreeIndexer(state);
		}
		System.out.println();
	}
	
	private static void printTreeIndexer(State state) {
		ArrayList<State> bp = state.backPointers.get(0);
		if (bp.size()==0) {
			System.out.println(state.id+": "+state.lhs+", "+state.completed.get(0)[0]);
		} else {
			System.out.println(state.id+": "+state.lhs+", "+state.bpListStrFormat());
		}
	}
}
