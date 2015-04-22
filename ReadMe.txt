# Earley Parser by hkh 2015-04-18

# 1. 실행 방법

	1) 파싱 대상 문구를 input.txt 파일에 저장합니다.
	
	2) 파싱 시 사용할 문법은 grammar.txt 파일에 저장합니다.
	
	3) 프로그램 실행 시 EarleyMain.class를 실행합니다.
	
	4) 구문 분석 결과는 output.txt 파일에 저장되고, 분석 과정은 console에 출력합니다.

	
# 2. 프로그램 내부 구조.

	1) 본 프로그램의 코어 로직은 EarleyParser 클래스로 추상화 되었습니다.

		EarleyParser parser = new EarleyParser(sentence);
		parser.setGrammar(grammar);
		parser.parse();
		
	2) EarleyParser#parse() 메서드가 실행되면 파싱 알고리즘을 수행합니다.
	
		Chart.class, State.class 두 클래스는 Earley Parsing 알로리즘에 필요한 데이터 구조를 담당합니다. 
		
		Chart 클래스는 현재 파싱 위치에 해당하는 State 목록을 저장하는 역할을 합니다.
			
			1. List<State> processed - 이미 처리된 State 들을 관리합니다. (중복 추가 방지)
			
			2. Queue<State> stateQueue - 처리해야할 State 들을 관리합니다.
			
		State 클래스는 Earley Parsing 알고리즘의 dotted rule를 구현한 클래스입니다.
		
			Earley Parsing 알고리즘은 다음과 같은 구조로 구현이 가능합니다.
			1. String[] completed				- 이미 찾은 규칙
			2. ArrayList<State> backPointers 	- back pointer 추적
			
			
			Tomita parser의 packing 매커니즘을 적용하기 위해 위의 구조를 다음과 같이 변경했습니다.
			1. ArrayList<String[]> completed	
			2. ArrayList<ArrayList<State>> backPointers
	
	
# 3. Ambiguity 예로 사용한 문법과 문구는 input_test.txt, grammar_test.txt에 있습니다.

# 4. 기타 자세한 사항은 프로그램에 주석처리로 명시했습니다. 


