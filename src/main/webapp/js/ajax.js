function post(url, data, sus, err){
	$.ajax({
		type:"POST",
		url:url,
		data:data,
		async:true,
//		contentType: "application/json;charset=utf-8",
		dataType: 'json',
		beforeSend:function(request){
			request.setRequestHeader("osType", "1");
			request.setRequestHeader("userId", window.localStorage.getItem("userId"));
			request.setRequestHeader("token", window.localStorage.getItem("token"));
		},
		success: function(re){
			if(login(re)){
				sus(re);
			}else{
				log("未登陆或登陆已过期")
				pHref("./login.html");
			}
			sus(re);
		},
		error: function(er){
			err(er)
		}
	})
}

function postBody(url, data, sus, err){
	$.ajax({
		type:"POST",
		url:url,
		data : JSON.stringify(data),
		async:true,
		contentType: "application/json;charset=utf-8",
		dataType: 'json',
		beforeSend:function(request){
			request.setRequestHeader("osType", "1");
			request.setRequestHeader("userId", window.localStorage.getItem("userId"));
			request.setRequestHeader("token", window.localStorage.getItem("token"));
		},
		success: function(re){
			if(login(re)){
				sus(re);
			}else{
				log("未登陆或登陆已过期")
				pHref("./login.html");
			}
		},
		error: function(er){
			err(er)
		}
	})
}



function postFile(url, roomNumber, formData, sus, err){
	$.ajax({
		type:"POST",
		url:url,
		data : formData,
		async:true,
		contentType: false,
		processData : false,
		dataType: 'json',
		beforeSend:function(request){
			request.setRequestHeader("osType", "1");
			request.setRequestHeader("userId", window.localStorage.getItem("userId"));
			request.setRequestHeader("token", window.localStorage.getItem("token"));
			request.setRequestHeader("roomNumber", roomNumber);
		},
		success: function(re){
			if(login(re)){
				sus(re);
			}else{
				log("未登陆或登陆已过期")
				pHref("./login.html");
			}
		},
		error: function(er){
			err(er)
		}
	})
}

function getFormJsonData(formId){
	var d = {}
	var t = $("#" + formId).serializeArray();
	
	$.each(t, function(){
		d[this.name] = this.value;
	})
	return d;
}

function getFormJsonStringData(formId){
	var j = getFormJsonData(formId);
	return JSON.stringify(j);
}


