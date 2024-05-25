
var accountHistoryTbody = document.getElementById('accountHistoryTbody');
var crewAccount = document.getElementById('crewAccount').value;
var inquiryReq = $.ajax({
    url: "/develop/openbank/using/transaction_list",
    method: "POST",
    data: {
        searchType:0,
        selectedAccount:''+crewAccount,
        content:'',
        startDate:'',
        endDate:''
    }
})



inquiryReq.done(function(result) {

    // 날짜를 기준으로 정렬
    result.sort(function(a, b) {
        var dateA = new Date(a.date);
        var dateB = new Date(b.date);
        return dateB - dateA; // 최신 날짜를 기준으로 정렬
    });

    var html = "";
    var stat = 0;

    result.forEach(function (dto) {

        console.log("stat : " + stat);
        if(stat>=9) {
            return;
        }

        var transferDateStr = dto.date;
        var transferDate = new Date(transferDateStr);

        var year = (transferDate.getFullYear()+'').slice(2,4);
        var month = transferDate.getMonth()+1;
        if(month<10) {
            month = '0' + month;
        }
        var date = transferDate.getDate();
        if(date<10) {
            date = '0' + date;
        }

        transferDateStr = year + '.' + month + '.' + date;
        console.log("assort: " + dto.assort);
        var assorts = "";

        if((dto.assort).trim()==='I') {
            assorts = "입금";
        } else {
            assorts = "출금";
        }


        html += "<tr>" +
            "<td>" + transferDateStr + "</td>" +
            "<td>" + assorts +"</td>" +
            "<td>" + dto.content + "</td>";

        if(assorts==="입금") {
            html += "<td style='text-align: right'>" + dto.inMoney.toLocaleString('ko-KR') + "</td>" +
                "<td></td>"
        } else {
            html += "<td></td>" +
                "<td style='text-align: right'>" + dto.outMoney.toLocaleString('ko-KR') + "</td>";
        }

        html += "<td style='text-align: right'>" + dto.balance.toLocaleString('ko-KR') + "</td>" +
            "</tr>";

        stat++;
    })

    accountHistoryTbody.innerHTML = html;
})