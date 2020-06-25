function login(res){
	if(res == 'undefined'){
		pHref("./login.html");
		return false;
	}
	var code = res.code;

	if(code == "1002"){
		//1002用户未登录
		clearCookie();
		pHref("./login.html");
		return false;
	}else if(code == "1003"){
		//1003登录已过期
		clearCookie();
		pHref("./login.html");
		
		return false;
	}else{
		return true;
	}
}

function userUnLogin(){
//	return loginInfoEmpty();
	return false;
}

function checkHtml(){
	var currentHtml = window.location.href;
	log("currentHtml = " + currentHtml)
	if(userUnLogin()){
		pHref("./login.html");
		href("./login.html");
		window.location.reload()
		return true;
	}
	
	var userIdentify = getUserIdentify();
	if(userIdentify == 0){
		if(currentHtml.indexOf("index.html") > -1){
			return false;
		}else{
			var pHtml = window.parent.location.href;
			log("pHtml = " + pHtml);
			if(pHtml.indexOf("index.html") > -1){
				return false;
			}
			href("./index.html");
		}
		
		return true;
	}
	
	if(userIdentify == 1){
		if(currentHtml.indexOf("room_list_for_user.html") > -1){
			return false;
		}
		if(currentHtml.indexOf("chat_room_teacher.html" > - 1)){
			return false;
		}
		href("./room_list_for_user.html");
		return true;
	}
	
	if(userIdentify == 2){
		if(currentHtml.indexOf("room_list_for_user.html") > -1){
			return false;
		}
		if(currentHtml.indexOf("chat_room_student.html") > -1){
			return false;
		}
		href("./room_list_for_user.html");
		return true;
	}
	return false;
}

function loginInfoEmpty(){
//	var uid = $.cookie('userId');
//	var token = $.cookie("token");

	var uid = window.localStorage.getItem("userId");
	var token = window.localStorage.getItem("token");
	
	log("userId = " + uid + "<--->token = " + token);

	if(uid == null || uid == "undefined" || uid == "" || uid == "null"
		|| token == null || token == "undefined" || token == "" || token == "null"){
			
		clearCookie();
		
		return true;
	}
		return false;
	
}

function unlogin(){
		pHref("./login.html");
}

function clearCookie(){
	window.localStorage.removeItem("token");
	window.localStorage.removeItem("userId");
	window.localStorage.removeItem("userIdentify");
	window.localStorage.removeItem("realName");
}

function saveCookie(token, userId){
	window.localStorage.setItem("token", token);
	window.localStorage.setItem("userId", userId);
}

function saveUserIdentify(userIdentify){
	window.localStorage.setItem("userIdentify", userIdentify);
}

function getUserIdentify(){
	return window.localStorage.getItem("userIdentify");
}

function getUserId(){
	return window.localStorage.getItem("userId");
}

function saveLoginInfo(userName, pwd){
	window.localStorage.setItem("userName", userName);
	window.localStorage.setItem("pwd", pwd);
}
function saveRealName(realName){
	window.localStorage.setItem("realName", realName);
}

function getUserName(){
	var userName = window.localStorage.getItem("userName");
	return (userName == null || userName == "") ? "" : userName;
}
function getPwd(){
	var pwd = window.localStorage.getItem("pwd");
	return (pwd == null || pwd == "") ? "" : pwd;
}

function getRealName(){
	var realName = window.localStorage.getItem("realName");
	return (realName == null || realName == "" || realName == undefined) ? getUserName() : realName;
}

function getName(){
	return getRealName() + "(" + getUserName() + ")"
}
