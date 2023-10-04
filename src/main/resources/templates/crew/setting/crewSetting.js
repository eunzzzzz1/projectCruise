
// '위로' 눌렀을 때 화면 상단으로 이동 ---------------------------------------------

    var upScrollText = document.getElementById('up');
    var blueDiv = document.getElementById("blue");
    var settingDetailConDiv = document.getElementById("settingDetailContainer");

    upScrollText.addEventListener('click',function(){
        window.scrollTo({left:0, top:0,behavior:'smooth'});

    })

// 화면 가장 상단일 때  -------------------------------------------

    window.addEventListener('scroll',function(){

        var scrollTop = window.scrollY || document.documentElement.scrollTop;


        if(scrollTop===0) {
            //설정창 div 숨기기
            if(blueDiv.classList.contains('visibleBlue')){
                blueDiv.classList.remove('visibleBlue');
                blueDiv.classList.add('hidden');
            }

            if(settingDetailConDiv.classList.contains('settingDetailConVisible')){
                settingDetailConDiv.classList.remove('settingDetailConVisible');
                settingDetailConDiv.classList.add('hidden');
            }


            // 위로가기를 누른 후 다시 각 메뉴를 눌렀을 때,
            // 마지막에 클릭한 설정창이 보여지는 현상이 발생함...
            // 위로가기를 누른 후 다시 각 메뉴를 눌렀을 때,
            // 각 메뉴의 첫번째 화면이 보이도록 설정
            var crewSettingTitle = document.getElementById("crewSettingTitle");
            var memSettingTitle = document.getElementById("memSettingTitle");
            var crewSettingContent = document.getElementById('crewSettingContent');
            var memSettingContent = document.getElementById('memSettingContent');

            crewSettingTitle.textContent = "크루 일정 관리";
            memSettingTitle.textContent = "월별 회비 조회";

            var crewSettingDivs = crewSettingContent.querySelectorAll("div");
            crewSettingDivs.forEach(function(crewSettingDiv){
                if(crewSettingDiv.id==="scheduleSet") {
                    crewSettingDiv.classList.add('visibleSettings');
                    crewSettingDiv.classList.remove('hidden');
                } else {
                    crewSettingDiv.classList.add('hidden');
                    crewSettingDiv.classList.remove('visibleSettings');
                }
            });

            var memSettingDivs = memSettingContent.querySelectorAll("div");
            memSettingDivs.forEach(function(memSettingDiv){
                if(memSettingDiv.id==="monthPayView") {
                    memSettingDiv.classList.add('visibleSettings');
                    memSettingDiv.classList.remove('hidden');
                } else {
                    memSettingDiv.classList.add('hidden');
                    memSettingDiv.classList.remove('visibleSettings');
                }
            });


            // 위로가기를 누른 후 다시 각 메뉴를 눌렀을 때,
            // 직전에 선택했던 Tap에 select 클래스가 덧씌워져 있음.
            // 첫 Tap에만 select가 되도록 다시 설정

            var crewTapsCon = document.getElementById('crewTap');
            var memberTapCon = document.getElementById('memberTap');

            var crewTaps = crewTapsCon.querySelectorAll("div")
            var memberTaps = memberTapCon.querySelectorAll("div")

            crewTaps[0].classList.add('select');
            for(var i = 1; i<crewTaps.length; i++) {
                crewTaps[i].classList.remove('select');
            }

            memberTaps[0].classList.add('select');
            for(var i = 1; i<memberTaps.length; i++) {
                memberTaps[i].classList.remove('select');
            }


        }

    })





// 버튼을 눌렀을 때, 파도의 밑부분이 늘어나며 아래로 이동 ------------------------------


    function selectCrewSetting() {
        var scrollPoint = document.getElementById('scrollPoint');
        var crewBoxDiv = document.getElementById('crewSettingBox');
        var memberBoxDiv = document.getElementById('memberSettingBox');
        var crewTapDiv = document.getElementById('crewTap');
        var crewFirstTap = crewTapDiv.querySelector("div");
        var memberTapDiv = document.getElementById('memberTap');
        selectSettingMenu();

        if(crewBoxDiv.classList.contains('hidden')){
            crewBoxDiv.classList.remove('hidden');
            crewBoxDiv.classList.add('visibleBox');
        }

        if(crewTapDiv.classList.contains('hidden')){
            crewTapDiv.classList.remove('hidden');
            crewTapDiv.classList.add('tapVisible')
        }

        if(memberBoxDiv.classList.contains('visibleBox')){
            memberBoxDiv.classList.remove('visibleBox');
            memberBoxDiv.classList.add('hidden');
        }

        if(memberTapDiv.classList.contains('tapVisible')){
            memberTapDiv.classList.remove('tapVisible');
            memberTapDiv.classList.add('hidden')
        }

        scrollPoint.scrollIntoView({behavior:'smooth'});

        // 선원 관리 탭에 들어갔다가 다시 크루 관리 탭에 들어왔을 때,
        // 가장 첫 번째 탭에 select 클래스가 설정되어 있지 않은 현상을 해결하기 위함
        if(!crewFirstTap.classList.contains('select')){
            crewFirstTap.classList.add('select')
        }

    }

    function selectMemberSetting() {
        var scrollPoint = document.getElementById('scrollPoint');
        var crewBoxDiv = document.getElementById('crewSettingBox');
        var memberBoxDiv = document.getElementById('memberSettingBox');
        var crewTapDiv = document.getElementById('crewTap');
        var memberTapDiv = document.getElementById('memberTap');
        var memberFirstTap = memberTapDiv.querySelector("div");
        selectSettingMenu();

        if(memberBoxDiv.classList.contains('hidden')){
            memberBoxDiv.classList.remove('hidden');
            memberBoxDiv.classList.add('visibleBox');
        }

        if(memberTapDiv.classList.contains('hidden')){
            memberTapDiv.classList.remove('hidden');
            memberTapDiv.classList.add('tapVisible')
        }


        if(crewBoxDiv.classList.contains('visibleBox')){
            crewBoxDiv.classList.remove('visibleBox');
            crewBoxDiv.classList.add('hidden');
        }

        if(crewTapDiv.classList.contains('tapVisible')){
            crewTapDiv.classList.remove('tapVisible');
            crewTapDiv.classList.add('hidden')
        }

        scrollPoint.scrollIntoView({behavior:'smooth'});

        // 크루 관리 탭에 들어갔다가 다시 선원 관리 탭에 들어왔을 때,
        // 가장 첫 번째 탭에 select 클래스가 설정되어 있지 않은 현상을 해결하기 위함
        if(!memberFirstTap.classList.contains('select')){
            memberFirstTap.classList.add('select')
        }
    }


    function selectSettingMenu() {

        if(blueDiv.classList.contains('hidden')){
            blueDiv.classList.remove('hidden');
            blueDiv.classList.add('visibleBlue');
        }

        if(settingDetailConDiv.classList.contains('hidden')){
            settingDetailConDiv.classList.remove('hidden');
            settingDetailConDiv.classList.add('settingDetailConVisible');
        }

    }



// 설정창에서 각 탭을 눌렀을 때의 액션들 ------------------------------

    // 선택된/선택되지 않은 탭의 색을 변하게 하는 자바스크립트
    var tapDivs = document.querySelectorAll(".tap");

    tapDivs.forEach(function(tapDiv) {
        tapDiv.addEventListener('click',function() {
            tapDiv.classList.add('select')

            tapDivs.forEach(function(otherTapDiv) {
                if (otherTapDiv !== tapDiv) {
                    otherTapDiv.classList.remove('select');
                }
            });
        })
    })


    // 탭 선택 시


    function setCrewTapClick(setMenuName) {
        var crewSettingTitleDiv = document.getElementById("crewSettingTitle");
        var crewSettingContentDiv = document.getElementById("crewSettingContent");
        var tapDivs = crewSettingContentDiv.querySelectorAll("div");
        var selectedTap = document.getElementById(setMenuName);
        var selectTapDivs = selectedTap.querySelectorAll("div");

        switch (setMenuName) {
            case "scheduleSet" :
                crewSettingTitleDiv.textContent = "크루 일정관리";
                break;

            case "informationSet" :
                crewSettingTitleDiv.textContent = "크루 정보수정";
                break;

            case "crewDelete" :
                crewSettingTitleDiv.textContent = "항해 중단하기";
                break;
        }

        tapDivs.forEach(function(tapDiv){
            if(tapDiv.id===setMenuName) {
                tapDiv.classList.remove('hidden');
                tapDiv.classList.add('visibleSettings');
            } else {
                tapDiv.classList.remove('visibleSettings');
                tapDiv.classList.add('hidden');
            }
        })

        for(var i=0;i<selectTapDivs.length;i++) {
            selectTapDivs[i].classList.remove('hidden');
        }

    }

    function setMemTapClick(setMenuName) {

        var memSettingTitle = document.getElementById("memSettingTitle");
        var memSettingContent = document.getElementById("memSettingContent");
        var tapDivs = memSettingContent.querySelectorAll("div");
        var selectedTap = document.getElementById(setMenuName);
        var selectTapDivs = selectedTap.querySelectorAll("div");

        switch (setMenuName) {
            case "monthPayView" :
                memSettingTitle.textContent = "월별 회비 조회";
                break;

            case "payRequest" :
                memSettingTitle.textContent = "선원 회비요청";
                break;

            case "memberBan" :
                memSettingTitle.textContent = "선원 강퇴";
                break;
        }

        tapDivs.forEach(function(tapDiv){
            if(tapDiv.id===setMenuName) {
                tapDiv.classList.remove('hidden');
                tapDiv.classList.add('visibleSettings');
            } else {
                tapDiv.classList.remove('visibleSettings');
                tapDiv.classList.add('hidden');
            }
        })

        for(var i=0;i<selectTapDivs.length;i++) {
            selectTapDivs[i].classList.remove('hidden');
        }
    }

