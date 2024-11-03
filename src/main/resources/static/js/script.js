// 모달 열기 함수
function openModal(movieId) {
    // 영화 정보를 가져오는 예시 (임의의 데이터 사용)
    document.getElementById("movieTitle").innerText = "천사와 악마";
    document.getElementById("movieOverview").innerText = "CERN에서 에너지원을 개발하며 발생하는 사건을 다룬 스릴러 영화입니다.";
    document.getElementById("releaseYear").innerText = "2009";
    document.getElementById("rating").innerText = "6.7";

    // 모달 열기
    document.getElementById("movieModal").style.display = "block";
}

// 모달 닫기 함수
function closeModal() {
    document.getElementById("movieModal").style.display = "none";
}
