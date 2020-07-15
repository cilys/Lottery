$(document).ready(function(){
	if(loginInfoEmpty()){
		reloadParent()
		return;
	}
	
	var startAllAppoint = 0;	//开始页数
	var currentPage = 1;	//当前页数
	var pageSize = 10
	var totalPage = 0;			//数据总条数
	var status = "";
	
	init();
	
	function init(){
		layui.use('form', function() {
			var form = layui.form;
			form.on('submit(search)', function(data) {
				getDatas();
				
				return false;
			});
		});
	}
	
	getDatas()
	
	
	function getDatas(){
		post(getHost() + "sys/cash/query",
		{
			pageNumber: currentPage
			, pageSize : pageSize
			, status : $("#select_status").val()
		}, function success(res){
			showToast(res.msg);
			if(res.code == 0){
				setDataToView(res.data.list);
				totalPage = res.data.totalRow;
				currentPage = res.data.pageNumber;
				toPage();
			}
		}, function error(err){
                showToast("获取列表失败");
		});
	}
	
	function setDataToView(data){
		var s = "<thead>"
				+ 	"<tr>"
				+		"<th>用户姓名</th>"
				+		"<th>提现金额</th>"
				+		"<th>申请时间</th>"
				+		"<th>提现进度</th>"
				+		"<th>提现备注</th>"
            	+		"<th>操作人员</th>"
            	+		"<th>操作备注</th>"
				+		"<th>操作</th>"
				+	"</tr>"
				+ "</thead>";
		
		if(data.length > 0){
			
			s += "<tbody>";
			
			$.each(data, function(v, o) {
				s += "<tr>";
				
				s += 	"<td>" + strFomcat(o.realName) + "</td>";
				s += 	"<td>" + o.money + "</td>";
				s += 	"<td>" + strFomcat(o.applyTime) + "</td>"
                s += 	"<td>" + fomcatCashStatus(o.status) + "</td>"
                s += 	"<td>" + fomcatMsg(o.msg) + "</td>"
				s += "<td>" + strFomcat(o.operatorName) + "</td>";
				s += "<td>" + fomcatMsg(o.operatorResult) + "</td>";
				s += "<td>"
					+	"<div class='layui-btn-group'>"
					+ 		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_edit' data-id='" + o.id + "' data-all='" + JSON.stringify(o) + "'>"
					+			"<i class='layui-icon'>&#xe642;</i>"
					+		"</button>"
					+	"</div>"
					+"</td>"
				
				s += 	"</tr>";
			});
			
			s += "</tbody>"
			
			$("#t_datas").html(s)
			
			$("#t_datas #btn_edit").on('click', function(){
				var id = $(this).attr("data-id");
				var params = $(this).attr("data-all")
				
				showDialog("审批/拒绝申请", "./cash_edit.html?params=" + encodeURI(params));
				
			})
            $("#paged").show();
		} else{
			$("#paged").hide();
			$("#t_datas").html("<br/><span style='width:10%;height:30px;display:block;margin:0 auto;'>暂无数据</span>");
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
	   					getDatas()
	   				}
	   			}
	   		});
   		});
	};
	
	function del(id){
		post(getHost() + "sys/cash/del", {
			id : id
		}, function success(res){
            showToast(res.msg)
			
			if(res.code == 0){
				getDatas()
			}
		}, function error(err){
            showToast("删除失败，请重试...")
		})
	}
	
	function fomcatCashStatus(status){
		if(status == 0) {
			return "已同意";
		} else if(status == 1){
			return "待审批";
		} else if(status == 2){
			return "已驳回";
		}
		return status;
	}
	
	function fomcatMsg(msg){
		if(msg != null && msg != undefined && msg.length > 15){
			msg = msg.substring(0, 15);
			msg = msg + "..."
		}
		if(msg == null && msg == undefined){
			return "";
		}
		return msg;
	}
})