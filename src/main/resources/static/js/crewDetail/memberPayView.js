var memberPayViewBoxDiv = document.getElementById("memberPayViewBox");
var payTotalDiv = document.getElementById('payTotalSum');
var payTotalCountDiv = document.getElementById('payTotalCount');

var crewNum = document.getElementById('crewNum').value;
var crewAccount = document.getElementById('crewAccount').value;


// 멤버와 회비내역 데이터 불러오기
// 멤버 이름 배열
var memNames = [];

//멤버 이름 데이터 구하기 function
var memberCount = 0; // 멤버 수
function setMemNames(memberList) {
    memberList.forEach(function (member) {
        memNames.push(member.MEM_NAME);
        memberCount++;
    })
}

var transferContents = []; // 각 멤버별로 검색 시 사용할 거래내용(입금자명) 세팅
var memberPayMoney = []; // 각 멤버별 입금내역

function setMemPayMoney(memberList, fullDates) {

    // 거래내용 세팅
    // crewNum+userNum+"_"+userName
    memberList.forEach(function (member){
        var memberName = member.MEM_NAME;
        var memberNum = member.MEM_NUM;
        transferContents.push(crewNum+memberNum+"_"+memberName);
    })

    // 멤버별 입금내역 세팅
    // 한 달간 모든 멤버의 입금액수가 한 객체에 담긴다.
    // memberPayMoney 에는 각 월별 내역이 담긴 5개의 객체가 담긴다
    for(var i=0;i<fullDates.length;i++) {

        memberPayMoney[i] = [];

        // 검색 시 사용할 날짜 세팅
        var startDate
            = new Date(fullDates[i].getFullYear(),fullDates[i].getMonth(), 1);
        var endDate
            = new Date(fullDates[i].getFullYear(),fullDates[i].getMonth() +1, 0);

        var startMonth = startDate.getMonth() +1 ;
        var endMonth = endDate.getMonth() +1 ;
        if(startMonth<10) {
            startMonth = '0'+startMonth;
        }
        if(endMonth<10) {
            endMonth = '0'+endMonth;
        }

        var startDateStr
            = startDate.getFullYear() + "-" + startMonth  + "-01 00:00:00";
        var endDateStr
            = endDate.getFullYear() + "-" + endMonth  + "-" + endDate.getDate() + " 23:59:59" ;

        // 멤버별로 입금액을 검색해, memName과 payMoney를 한 객체안에 담는다.
        for(var j=0;j<memberList.length;j++) {

            let thisMemName = memberList[j].MEM_NAME;
            let thisMemEmail = memberList[j].MEM_EMAIL.split("@")[0];

            if(thisMemEmail.length>10) {
                thisMemEmail = thisMemEmail.slice(0,8);
                thisMemEmail = thisMemEmail + "...";
            }

            let thisMemFullEmail = memberList[j].MEM_EMAIL;
            let transferContent = transferContents[j];
            console.log(thisMemName + " / " + transferContent);
            console.log(crewAccount);

            var inquieyReq = $.ajax({
                url: "/develop/openbank/using/transaction_list",
                method: "POST",
                data: {
                    searchType:4,
                    selectedAccount:crewAccount+'',
                    content:transferContent,
                    startDate:startDateStr,
                    endDate:endDateStr
                }
            })

            let payData = {};

            inquieyReq.done(function(result){
                payData.memName = thisMemName;
                payData.memEmail = thisMemEmail;
                payData.memFullEmail = thisMemFullEmail;
                payData.payMoney = result[0].inMoney;
            })

            // 멤버 한 명의 객체를 memberPayMoney의 i번째에 담는걸 반복한다.
            // i번째에는 멤버 별 입금액이 담겨있다.
            memberPayMoney[i].push(payData);
        }
    }
}

function monthSelectorChange(selectedOption) {

    var selectedMonthList = memberPayMoney[selectedOption];

    //memNames 배열에서 selectedMonthList 배열에 없는 값을 찾아내기
    // 거래내역이 없으면 검색이 안 됐을것이기 때문에!


    // 리스트 html 쓰기
    var html = "";
    var payTotal = 0;

    var userIsCaptain = document.getElementById('userIsCaptain').value;
    var selectedMemStat = 0;

    for(var i=0;i<selectedMonthList.length;i++) {

        if (selectedMonthList[i].payMoney===0) {
            html +=
                "<a class='highlighter' onclick='highlighterClick(" + userIsCaptain + ")'> <div class='unpaid_record'>" +
                "<div class='name'>"
                + selectedMonthList[i].memName
                + " (" + selectedMonthList[i].memEmail + ") </div>" +
                "<div class='money'> 0원 </div>" +
                "</div></a>";

            selectedMemStat++;

        } else {
            html +=
                "<div class='record'>" +
                "<div class='name'>" + selectedMonthList[i].memName + " (" + selectedMonthList[i].memEmail + ") </div>" +
                "<div class='money'>" + selectedMonthList[i].payMoney.toLocaleString('ko-KR') + "원 </div>" +
                "</div>";
        }

    }
    //.toLocaleString('ko-KR') = 숫자 쉼표표시

    memberPayViewBoxDiv.innerHTML = html;

    // 총액 html 쓰기
    for(var i=0;i<selectedMonthList.length;i++) {
        payTotal += selectedMonthList[i].payMoney;
    }
    payTotalDiv.textContent
        = payTotal.toLocaleString('ko-KR') + '원';
    payTotalCountDiv.textContent
        = (memberCount - selectedMemStat) + "명";



    var notPayMember = document.querySelectorAll('.highlighter');
    // 함수 밖에서 notPayMember를 초기화해주면
    // 문서가 로딩되었을 때 불러들여진 highlighter 클래스로만 초기화되는듯.
    // 함수가 호출될 때 마다 notPayMember를 선언해주어야
    // 달이 바뀌었을 때 해당하는 달의 highlighter div로 초기화가 된다.

    notPayMember.forEach(function(member) {
        member.style.cursor = 'pointer'
    })

}

var payViewMonthSelector = document.getElementById('payViewMonth');
payViewMonthSelector.addEventListener('change',function() {
    var selectedOption = this.value;
    console.log(selectedOption)
    monthSelectorChange(selectedOption);
})

function highlighterClick(isCaptain) {
    if(isCaptain) {
        if(confirm("선장님, 선원 관리 탭에서 회비를 요청해보세요.")) {
            window.location.href='/crew/setting?crewNum='+crewNum;
        } else {

        }
    }
}