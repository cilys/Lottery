function dismissDialog(){
	x_admin_close()
}

function showDialog(title, url){
	x_admin_show(title, url);
}

function reloadParent(){
	parent.window.location.reload()
}

/**
 * 两个按钮 + 输入框的html代码
 */
function doubleBtnInputDialogHtml(){
	var s = '';
	s +=	'<div class="layui-form-item" style="padding:10px">'
		+		'<div class="layui-form-item">'
		+			'<label for="input_remark" class="layui-form-label">'
		+				'操作备注：'
		+			'</label>'
		+			'<div class="layui-input-block" style="margin-bottom:5px">'
		+				'<input type="text" id="input_remark" name="mask" class="layui-input">'
		+		'	</div>'
		+		'</div>'
		+		'<div style="margin-bottom:10px">'
		+			'<span class="x-red">此操作可能会影响账户余额，请确认是否已经真实完成线下金钱交易！！！</span>'
		+		'</div>'
		+		'<button id="btn_pay" class="layui-btn">支付状态</button>'
		+		'<button id="btn_unpay" class="layui-btn">未付状态</button>'
		+		'<button id="btn_back" class="layui-btn">退款状态</button>'
		+	'</div>'
	return s;
}
