package com.kmg.udemyreactor.sec03


// 프로세서는 pub, sub 인터페이스를 다 구독한거임. Producer 역할의 업스트림의 요청을 받아서 처리한 후 subscriber 역할의 다운스트림에 보냄.
// log() 는 디버깅 가능. 단 위의 논리에 따라 위아래만 로깅함. 필요한 레이어마다 붙여야함
// Range.. 뭐 간단.
// List 와 다른점은 사용가능한 데이터를 바로바로 받을 수 있는 것과 취소가 가능한 것.
// Mono 와 Flux 는 Data Structure 가 아니다. list, set 이런건 데이터를 저장하고, 통째로 옮길 수 있는 컨테이너다. Mono, Flux 는 파이프라인이지 저장하지 않는다.
class `05~08` {
}