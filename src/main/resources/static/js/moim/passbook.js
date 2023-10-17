
function sendIt(){
    f = document.myForm;

    str = f.crewName.value;
    str = str.trim();
    if(!str){
        alert("\n모임통장 이름을 입력하세요.");
        f.crewName.focus();
        return;
    }
    f.crewName.value = str;

    str = f.crewPaymoney.value;
    str = str.trim();
    if(!str){
        alert("\n회비금액을 입력하세요.");
        f.crewPaymoney.focus();
        return;
    }
    f.crewPaymoney.value = str;

    str = f.crewPaydate.value;
    str = str.trim();
    if(!str){
        alert("\n납입일을 입력하세요.");
        f.crewPaydate.focus();
        return;
    }
    f.crewPaydate.value = str;

    let value = parseInt(f.crewPaydate.value, 10);
    if (isNaN(value) || value < 1 || value > 31) {
    alert("1~31 범위로 입력하세요.");
    f.crewPaydate.value = '';
    f.crewPaydate.focus();
     return; // 예외가 발생한 경우, 여기서 함수를 종료합니다.
    }

     f.crewPaydate.value = str;

    if(!f.consent.checked){
        alert("\n약관에 동의하세요");
        f.consent.focus();
        return;
    }

     // 필수 입력 항목이 모두 입력되었을 때만 모달 창을 엽니다.
    const modal = document.getElementById("modal")
    modal.style.display = "flex";
}

	const closeBtn = document.querySelector(".close-area")
	closeBtn.addEventListener("click", e => {
    const modal = document.getElementById("modal")
    modal.style.display = "none"
    window.location.href = "http://localhost:8082/mypage/mypage_all";

});

//체크박스 선택시 활성화(기존계좌)
function myAccount(checkbox) {

    const textbox_elem = document.getElementById('my_account');
    textbox_elem.disabled = checkbox.checked ? false : true;

    if(textbox_elem.disabled) {
        textbox_elem.value = null;
    } else {
        textbox_elem.focus();
    }

    // 새로운 계좌 선택 체크박스 선택 시 기존계좌 체크 해제
    const newAccountCheckbox = document.getElementsByName('account_choice')[1];
    if (checkbox.checked) {
        newAccountCheckbox.checked = false;
        // 새로운 계좌 버튼도 비활성화
        document.getElementById('new_account_button').disabled = true;
    }
}

//체크박스 선택시 활성화(새로운 계좌 버튼)
function newAccount(checkbox) {

    const textbox_elem = document.getElementById('new_account_button');
    textbox_elem.disabled = checkbox.checked ? false : true;

    if(textbox_elem.disabled) {
        textbox_elem.value = null;
    } else {
        textbox_elem.focus();
    }

    const myAccountCheckbox = document.getElementsByName('account_choice')[0];
    if (checkbox.checked) {
        myAccountCheckbox.checked = false;
        // 기존계좌 입력 필드도 비활성화
        document.getElementById('my_account').disabled = true;
    }
}

//체크박스 선택시 활성화(새로운 계좌 버튼)
function newAccount(checkbox) {

    const textbox_elem = document.getElementById('new_account_button');
    textbox_elem.disabled = checkbox.checked ? false : true;

    if(textbox_elem.disabled) {
        textbox_elem.value = null;
    } else {
        textbox_elem.focus();
    }

    const myAccountCheckbox = document.getElementsByName('account_choice')[0];
    if (checkbox.checked) {
        myAccountCheckbox.checked = false;
        // 기존계좌 입력 필드도 비활성화
        document.getElementById('my_account').disabled = true;
    }
}
function register(){
    let modal = document.getElementById("my_modal");
    modal.style.display = "block";
}

function closeModal() {
    let modal = document.getElementById("my_modal");
    modal.style.display = "none"; // 모달창 닫기
}