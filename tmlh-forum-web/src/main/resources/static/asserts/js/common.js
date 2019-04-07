function toDateTime(time){
	// 比如需要这样的格式 yyyy-MM-dd hh:mm:ss
	var date = new Date(time);
	Y = date.getFullYear() + '-';
	M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
	D = date.getDate() + ' ';
	h = date.getHours() + ':';
	m = date.getMinutes() + ':';
	s = date.getSeconds(); 
	return Y+M+D+h+m+s;
}

function diffTime(startDate) { 
    startDate= new Date(startDate);
    var endDate= new Date();
    var diff=endDate.getTime() + (3600*1000 * 20) - startDate.getTime();//时间差的毫秒数 
        
    //计算出小时数 
    var leave1=diff%(24*3600*1000);    //计算天数后剩余的毫秒数 
    var hours=Math.floor(leave1/(3600*1000)); 
    return hours; 
}    

function successNotify(msg){
	$.notify(
		{message:msg},
   		{type: 'success', spacing: 110}
	);
}
function warningNotify(msg){
	$.notify(
			{message:msg},
				{type: 'warning'}
	);
}
function warningNotify(msg ,url){
	$.notify({
	 message:msg,
		url: url,
 	},
{type: 'warning'});
}
			
function infoNotify(msg){
	$.notify(
		{message:msg},
		{type: 'info' , offset: 60}
	);
}
			
function dangerNotify(msg){
	$.notify(
		{message:msg},
		{type: 'danger'}
	);
}
function errorHandler(message){
	dangerNotify(message);
}