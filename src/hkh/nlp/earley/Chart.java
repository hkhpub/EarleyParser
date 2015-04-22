package hkh.nlp.earley;

import hkh.nlp.util.ArrayUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * a set of states 
 * @author hkh
 *
 */
public class Chart {

	// 처리할 state queue
	private Queue<State> stateQueue = null;
	
	// 이미 처리된 state list
	private List<State> processed = null;
	
	public Chart() {
		stateQueue = new ArrayDeque<State>();
		processed = new ArrayList<State>();
	}
	
	public void addState(State state) {
		if (isProcessed(state)) {
			return;
		}
		if (isQueued(state)) {
			return;
		}
		State s = findPackableState(state);
		if (s!=null) {
			// packing 할 수 있는 state가 존재한다.
			// state의 completed를 s의 completed와 packing한다.
			s.completed.addAll(state.completed);
			s.backPointers.addAll(state.backPointers);
//			System.out.println(">> packable1 >> "+s.toString());
//			System.out.println(">> packable2 >> "+state.toString());
			return;
		}
		
		state.id = State.getNewId();
		stateQueue.add(state);
	}
	
	/**
	 * 처리할 State return
	 * @return
	 */
	public State getState() {
		State state = stateQueue.poll();
		if (state != null) {
			processed.add(state);
		}
		return state;
	}
	
	/**
	 * 이미 처리된 state 여부
	 * @param state
	 * @return
	 */
	public boolean isProcessed(State state) {
		for (State s : processed) {
			if (State.sameState(s, state)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 이미 queue에 pending되어 있는지 여부
	 * @param state
	 * @return
	 */
	public boolean isQueued(State state) {
		for (State s : stateQueue) {
			if (State.sameState(s, state)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * [x, y, A, [B C], []]
	 * [x, y, A, [D E], []]
	 * completed state 중 x, y, A가 같고 B C, D E와 같이 derivation만 다른 경우 packable state 이다.
	 * @param state
	 * @return
	 */
	public State findPackableState(State state) {
		if (!state.isComplete())
			return null;
		for (State s : stateQueue) {
			if (s.start == state.start && s.current == state.current && s.isComplete()
					&& s.lhs.equals(state.lhs)) {
				return s;
			}
		}
		return null;
	}
	
	public List<State> getMatchedActiveStates(State state) {
		List<State> activeStates = new ArrayList<State>();
		for (State s : processed) {
			if (s.getNextString() == null) {
				throw new RuntimeException("Error: "+s.toString());
			}
			if (s.getNextString().equals(state.lhs)) {
				// matched
				String next = s.getNextString();
				String[] incompleted = ArrayUtil.removeFirst(s.incompleted);
				ArrayList<String[]> completed = new ArrayList<String[]>();
				ArrayUtil.appendElement(s.completed, completed, next);
				State newState = new State(s.start, state.current, s.lhs, completed, incompleted);
				
				newState.operation = "Completer";
				// TODO: s.addBackPointer(); 정확성여부 검사
				for (int i=0; i<s.backPointers.size(); i++) {
					ArrayList<State> bp1 = s.backPointers.get(i);
					ArrayList<State> bp2 = newState.backPointers.get(i);
					for (State bp_state : bp1) {
						bp2.add(bp_state);
					}
				}
				for (int i=0; i<newState.backPointers.size(); i++) {
					newState.backPointers.get(i).add(state);
				}
				
				activeStates.add(newState);
			}
		}
		return activeStates;
	}
	
	public List<State> getFinalStates() {
		return processed;
	}
}
