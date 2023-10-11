  // 세션 데이터를 JavaScript로 가져와서 모달에 출력하는 함수
       function showModalWithAjax() {
    var group = document.getElementById("sessionGroup").textContent;
    var email = document.getElementById("sessionEmail").textContent;
    var num = parseInt(document.getElementById("sessionNum").textContent);

    // AJAX 요청 보내기
    $.ajax({
        type: "GET",
        url: "/showModalWithAjax",
        data: {
            email: email,
            num: num
        },
        success: function(response) {
            // 서버에서 받은 응답을 처리
            if (response === "success") {
                // 모달 표시
                createModal(group);
            } else {
                // 비교에 실패한 경우 다른 처리 수행
                console.log("모임장또는 선원임");
            }
        },
        error: function() {
            console.error("모임장또는선원이거나 초대 거절");
        }
    });
}

        // 모달 창을 생성하는 함수
        function createModal(group) {
            var modal = document.createElement("div");
            modal.className = "modal";
            modal.innerHTML = `
                <div id="modal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Cruise 대표 이미지나 로고 같은거 넣을자리</h2>

        </div>
        <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/People/Family%20Man%2C%20Woman%2C%20Girl%2C%20Boy.png" alt="Family Man, Woman, Girl, Boy" width="200" height="200" />
        <p>${group} 모임 통장에 초대되었습니다.</p>
        <div class="modal-footer">
            <button class="accept" onclick="accept()">수락</button>
            <button class="reject" onclick="reject()">거절</button>
        </div>
    </div>
</div>
            `;
            document.body.appendChild(modal);
            modal.style.display = "block"; // 모달 창 표시
        }

        // 모달 창을 닫는 함수
        function closeModal() {
            var modal = document.querySelector(".modal");
            modal.style.display = "none"; // 모달 창 닫기
        }

        // 수락 버튼 클릭 시 동작하는 함수
        function accept() {
            // 수락을하면 세션에있는 크루 일련번호,선원이메일을가지고 crew_member테이블에 집어넣음  잘들어 가면 세션값 지우는걸로

            window.location.href="/accept";

            closeModal(); // 모달 창 닫기
        }

        // 거절 버튼 클릭 시 동작하는 함수
        function reject() {
            // 거절하면 세션값 지움 다시 모임에 들어가고 싶으면 모임장에게 다시 초대를 받아야 하는 시스템
            // 메시지를 보내는 모임장은 초대수락받을 필요가없음 <<이거해결해야함 크루 일련번호가 있으니 그걸 토대로 선장이메일 확인해서
            // 로그인한 사람 이메일과 선장이메일이 같으면 모달창 띄울 필요가 없음! 유레카!

            window.location.href="/reject";

            closeModal(); // 모달 창 닫기
        }

        window.onload = showModalWithAjax;