  Kakao.init('9862f7eff0dc73e6f37d21910ffc74b0');
     function sendLink() {
     var group = document.getElementById("sessionGroup").textContent;
     var num = document.getElementById("sessionNum").textContent;

        var mobileWebUrl = 'http://localhost:8082/?group=' + group + '&num=' + num;
       var webUrl = 'http://localhost:8082/?group=' + group + '&num=' + num;

       Kakao.Link.sendDefault({
         objectType: 'feed',
         content: {
           title: 'Cruise에 승선하기',
           description: '모임통장에 초대 받으셨습니다.',
           imageUrl:
             'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQueOKbvlKIICdO6hNkQQK0znwv80fATsUieQ&usqp=CAU',
           link: {
             mobileWebUrl: mobileWebUrl,
             webUrl: webUrl,
           },
         },
         social: {
           likeCount:50, //99999가 최대입니다
           commentCount: 32, //99999가 최대입니다
           sharedCount: 19, //99999가 최대입니다
         },
         buttons: [
           {
             title: '초대에 응하기',
             link: {
               mobileWebUrl: mobileWebUrl,
               webUrl: webUrl,
             },
           },
         ],
       })
     }