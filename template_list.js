$(document).ready(function(){
	var BASE_URL = getHost() + "user/";
	
	var startAllAppoint = 0;	//开始页数
	var currentPage = 1;	//当前页数
	var pageSize = 10
	var totalPage = 0;			//数据总条数
	var status = "";
	
	getDatas()
	
	
	function getDatas(){
		post(BASE_URL + "queryAll",
		{
			pageNumber: currentPage
			, pageSize : pageSize
			, status : status
		}, function success(res){
			showToast(res.msg);
			if(res.code == 0){
				setDataToView(res.data.list);
				totalPage = res.data.totalPage;
				currentPage = res.data.pageNumber;
				toPage();
			}
		}, function error(err){
			showToast("操作失败，请稍后重试..");
		});
	}
	
	function setDataToView(data){
		var s = "<thead>"
				+ 	"<tr>"
				+ 		"<th>登录账号</th>"
				+		"<th>真实姓名</th>"
				+		"<th>手机号码</th>"
				+		"<th>用户性别</th>"
				+		"<th>用户身份</th>"
				+		"<th>状态</th>"
				+		"<th>注册时间</th>"
				+		"<th>操作</th>"
				+	"</tr>"
				+ "</thead>";
		
		if(data.length > 0){
			
			s += "<tbody>";
			
			$.each(data, function(v, o) {
				s += "<tr>";
				
				s += 	"<td>" + o.userName + "</td>";
				s += 	"<td>" + strFomcatUserName(o.realName, o.userName) + "</td>";
				s += 	"<td>" + strFomcat(o.phone) + "</td>";
				s += 	"<td>" + fomcatSex(o.sex) + "</td>"
				s += 	"<td>" + fomcatUserIdentify(o.userIdentify) + "</td>"
				if(o.status == 0){
					s += "<td>";
					s +=		"<button id='btn_status_edit' class='layui-btn layui-btn-primary layui-btn-sm' data-status='" + o.status + "' data-id='" + o.userId + "'>";
					s +=			"<i class='layui-icon'>&#xe605;</i>";
					s +=		"</button>";
					s +=	 "</td>";
				} else{
					s += "<td>";
					s +=		"<button id='btn_status_edit' class='layui-btn layui-btn-primary layui-btn-sm' data-status='" + o.status + "' data-id='" + o.userId + "'>";
					s +=			"<i class='layui-icon'>&#x1006;</i>";
					s +=		"</button>";
					s +=	 "</td>";
				}
				s += "<td>" + o.createTime + "</td>";
				
				s += "<td>"
					+	"<div class='layui-btn-group'>"
					+ 		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_edit' data-id='" + o.userId + "' data-name='" + strFomcatUserName(o.realName, o.userName) + "'>"
					+			"<i class='layui-icon'>&#xe642;</i>"
					+		"</button>"
					+		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_del' data-id='" + o.userId + "'>"
					+			"<i class='layui-icon'>&#xe640;</i>"
					+		"</button>"
					+	"</div>"
					+"</td>"
				
				s += 	"</tr>";
			});
			
			s += "</tbody>"
			
			$("#t_user_list").html(s)
			
			
			$("#t_user_list #btn_status_edit").on('click', function(){
				var userId = $(this).attr("data-id");
				var status = $(this).attr("data-status");
				if(status == 0){
					layer.confirm('是否禁用该用户？',function(index){
		              	updateUserStatus(userId, 1)
					});
				}else {
					layer.confirm('是否启用该用户？',function(index){
		              //发异步删除数据
		              	updateUserStatus(userId, 0)
					});
				}
			})
			
			$("#t_user_list #btn_edit").on('click', function(){
				var userId = $(this).attr("data-id");
				var name = $(this).attr("data-name")
				showDialog("编辑用户【" + name + "】信息", "./user_edit.html?userId=" + userId);
			})
			
			$("#t_user_list #btn_user_role").on('click', function(){
				var userId = $(this).attr("data-id");
				var name = $(this).attr("data-name")
				showDialog("编辑用户【" + name + "】角色", "./user_role.html?userId=" + userId)
			})
			
			$("#t_user_list #btn_del").on('click', function(){
				var userId = $(this).attr("data-id");
				
				layer.confirm('是否删除该用户？(此操作不可恢复，请谨慎操作！)',function(index){
					del(userId)
				});
			})
		} else{
			$("#paged").hide();
			$("#t_user_list").html("<br/><span style='width:10%;height:30px;display:block;margin:0 auto;'>暂无数据</span>");
		}
	}
	
	function toPage(){
   		layui.use('laypage', function(){
   			var laypage = layui.laypage;
   			
   			laypage.render({
	   			elem : 'paged'
	   			, count : totalPage
	   			, limit : pageSize
	   			, curr : currentPage
	   			, jump : function(obj, first){
	   				currentPage = obj.curr;
	   				
	   				if(!first){
	   					getDatas();
	   				}
	   			}
	   		});
   		});
	};
	
	function delById(id){
		post(BASE_URL + "delById", {
			id : id
		}, function success(res){
			showToast(res.msg);
			
			if(res.code == 0){
				getDatas();
			}
		}, function error(err){
			showToast("删除失败，请重试...");
		})
	}
	
	function updateUserStatus(userId, status){
		post(getHost() + "sys/user/updateUserStatus",
		{
			userId : userId
			, status : status
		}, function success(res){
			showToast(res.msg);
			if(res.code == 0){
				getUsers()
			}
		}, function error(err){
			showToast("更新状态失败，请重试..");
		})
	}
	
	function fomcatUserIdentify(userIdentity){
		if(strIsEmpty(userIdentity)){
			return "";
		}
		if(userIdentity == 0){
			return "管理员";
		}
		if(userIdentity == 1){
			return "普通用户";
		}
		if(userIdentity == 2){
			return "普通用户";
		}
	}
})