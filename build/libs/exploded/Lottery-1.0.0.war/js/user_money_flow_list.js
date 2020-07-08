$(document).ready(function(){
	if(loginInfoEmpty()){
		reloadParent()
		return;
	}
	
	userId = getUrlParam("userId");
	if(userId == null || userId == undefined){
		userId = "";
	}
	
	var startAllAppoint = 0;	//开始页数
	var currentPage = 1;	//当前页数
	var pageSize = 10
	var totalPage = 0;			//数据总条数
	
	init();
	
	function init(){
		layui.use('form', function() {
			var form = layui.form;
			form.on('submit(search)', function(data) {
				getDatas();
				
				return false;
			});
			
			form.on('select(payStatus)', function(data) {
				currentPage = 1;
				
				return false;
			});
			form.on('select(payType)', function(data) {
				currentPage = 1;
				
				return false;
			});
		});
	}
	
	getDatas()

	function getDatas(){
		post(getHost() + "userMoneyFlow/query",
		{
			pageNumber: currentPage
			, pageSize : pageSize
			, payType : $("#select_pay_type").val()
			, isAddToUser : $("#select_is_add_to_user").val()
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
				+		"<th>方案名称</th>"
				+		"<th>总金额</th>"
				+		"<th>资金来源</th>"
				+		"<th>支付方式</th>"
            	+		"<th>资金状态</th>"
            	+		"<th>创建时间</th>"
				+		"<th>操作</th>"
				+	"</tr>"
				+ "</thead>";
		
		if(data.length > 0){
			
			s += "<tbody>";
			
			$.each(data, function(v, o) {
				s += "<tr>";
				
				s += 	"<td>" + strFomcat(o.userRealName) + "</td>";
				s += 	"<td>" + strFomcat(o.schemeName) + "</td>";
				s += 	"<td>" + o.money + "</td>"
                s += 	"<td>" + strFomcat(o.sourceUserName) + "</td>"
                s +=	"<td>" + fomcatPayType(o.payType) + "</td>"
                s += 	"<td>" + fomcatIsAddToUser(o.isAddToUser) + "</td>"
                s += 	"<td>" + o.createTime + "</td>"
				s += "<td>"
					+	"<div class='layui-btn-group'>"
					+		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_del' data-id='" + o.id + "'>"
					+			"<i class='layui-icon'>&#xe640;</i>"
					+		"</button>"
					+	"</div>"
					+"</td>"
				
				s += 	"</tr>";
			});
			
			s += "</tbody>"
			
			$("#t_datas").html(s)
			
			$("#t_datas #btn_del").on('click', function(){
				var id = $(this).attr("data-id");
				layer.confirm('是否删除该资金流水？(此操作不可恢复，请谨慎操作！)',function(index){
					del(id)
				});
			})
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
		post(getHost() + "sys/userMoneyFlow/del?id=" + id, {
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
	
	function fomcatIsAddToUser(isAddToUser){
		if(isAddToUser == 0){
			return "同步成功";
		} else if(isAddToUser == 1){
			return "已计算";
		} else if(isAddToUser == 3){
			return "未同步";
		}
		
		return isAddToUser;
	}
	
})