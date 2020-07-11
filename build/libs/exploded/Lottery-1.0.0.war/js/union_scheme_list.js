$(document).ready(function(){
	if(loginInfoEmpty()){
		reloadParent()
		return;
	}
	
	var startAllAppoint = 0;	//开始页数
	var currentPage = 1;	//当前页数
	var pageSize = 10
	var totalPage = 0;			//数据总条数
	var status = "0";
	
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
	
	
	
	Date.prototype.Format = function (fmt) {
	    var o = {
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "H+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	}
	

	function getDatas(){
		post(getHost() + "sys/scheme/queryAll",
		{
			pageNumber: currentPage
			, pageSize : pageSize
			, status : $("#select_status").val()
			, outTimeType : $("#select_outTimeType").val()
			, name : $("#input_name").val()
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
				+		"<th>方案名称</th>"
				+		"<th>总金额</th>"
				+		"<th>已售金额</th>"
				+		"<th>成交金额</th>"
				+		"<th>过期时间</th>"
            	+		"<th>状态</th>"
            	+		"<th>奖金总额</th>"
            	+		"<th>税率</th>"
            	+		"<th>可用奖金</th>"
				+		"<th>操作</th>"
				+	"</tr>"
				+ "</thead>";
		
		if(data.length > 0){
			
			s += "<tbody>";
			
			$.each(data, function(v, o) {
				s += "<tr>";
				
				s += 	"<td>" + strFomcat(o.name) + "</td>";
				s += 	"<td>" + o.totalMoney + "</td>";
				s += 	"<td>" + o.selledMoney + "</td>"
                s += 	"<td>" + o.payedMoney + "</td>"
                s += 	"<td>" + strFomcat(o.outOfTime) + "</td>"
				if(o.status == 0){
					s += "<td>";
					s +=		"<button id='btn_status_edit' class='layui-btn layui-btn-primary layui-btn-sm' data-status='" + o.status + "' data-id='" + o.id + "'>";
					s +=			"<i class='layui-icon'>&#xe605;</i>";
					s +=		"</button>";
					s +=	 "</td>";
				} else{
					s += "<td>";
					s +=		"<button id='btn_status_edit' class='layui-btn layui-btn-primary layui-btn-sm' data-status='" + o.status + "' data-id='" + o.id + "'>";
					s +=			"<i class='layui-icon'>&#x1006;</i>";
					s +=		"</button>";
					s +=	 "</td>";
				}
				s += "<td>" + o.totalBonus + "</td>";
				s += "<td>" + o.bonusRate + "</td>";
				s += "<td>" + o.canUseBonus + "</td>";
				s += "<td>"
					+	"<div class='layui-btn-group'>"
					+ 		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_edit' data-id='" + o.id + "' data-out-time='" + o.outOfTime + "' data-name='" + strFomcat(o.name) + "'>"
					+			"<i class='layui-icon'>&#xe642;</i>"
					+		"</button>"
					+ 		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_history' data-id='" + o.id + "' data-name='" + strFomcat(o.name) + "'>"
					+			"<i class='layui-icon'>&#xe628;</i>"
					+		"</button>"
					+		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_del' data-id='" + o.id + "'>"
					+			"<i class='layui-icon'>&#xe640;</i>"
					+		"</button>"
					+	"</div>"
					+"</td>"
				
				s += 	"</tr>";
			});
			
			s += "</tbody>"
			
			$("#t_datas").html(s)
			
			$("#t_datas #btn_status_edit").on('click', function(){
				var id = $(this).attr("data-id");
				var status = $(this).attr("data-status")
				if(status == 0){
					layer.confirm('是否禁用该方案？',function(index){
		              	updateStatus(id, 1)
		        	});
				}else {
					layer.confirm('是否启用该方案？',function(index){
		              //发异步删除数据
		              	updateStatus(id, 0)
		        	});
				}
			})
			
			
			$("#t_datas #btn_edit").on('click', function(){
				var id = $(this).attr("data-id");
				var name = $(this).attr("data-name")
				var outOfTime = $(this).attr("data-out-time");
				var currentTime = new Date().Format("yyyy-MM-dd HH:mm:ss");
				if(currentTime > outOfTime){
					showDialog("编辑方案【" + name + "】信息", "./union_scheme_edit_bonus.html?id=" + id);	
				}else{
					showDialog("编辑方案【" + name + "】信息", "./union_scheme_edit.html?id=" + id);
				}
			})

			$("#t_datas #btn_history").on('click', function(){
				var id = $(this).attr("data-id");
				var name = $(this).attr("data-name")
				showDialog("【" + name + "】购买记录", "./scheme_buy_list.html?id=" + id);
			})

			$("#t_datas #btn_del").on('click', function(){
				var id = $(this).attr("data-id");
				layer.confirm('是否删除该方案？(此操作不可恢复，请谨慎操作！)',function(index){
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
		post(getHost() + "sys/scheme/del?id=" + id, {
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
	
	function updateStatus(id, status){
//		post(getHost() + "sys/scheme/updateStatus?id=" + id + "&status=" + status,
		post(getHost() + "sys/scheme/updateStatus",
		{
			id : id
			, status : status
		}, function success(res){
			showToast(res.msg)
			if(res.code == 0){
				getDatas()
			}
		}, function error(err){
                showToast("更新状态失败，请重试..")
		})
	}
})