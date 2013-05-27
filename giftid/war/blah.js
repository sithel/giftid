console.log("This ran");

function start() {
	console.log("We started");
	console.log("Is there a hash? : "+window.location.hash);
	if(window.location.hash.length > 0) {
		handleLoad();
	}
	$('.button').click(handleSaveClick);
}
function handleLoad() {
	var successhandler = function(x) {
		console.log("SUCCESS : "+x);
		x = JSON.parse(x);
		if (!x.user1) {
			console.log("We got back shit");
			return;
		}
		$('[data-yarn=user1]').val(x.user1);
		$('[name=gotget]:checked').val(x.gotget);
		$('[data-yarn=gift]').val(x.gift);
		$('[name=forfrom]:checked').val(x.forfrom);
		$('[data-yarn=user2]').val(x.user2);
	}
	var errorhandler = function(x) {
		console.log("FAILURE: "+x);
	}
	var hash = window.location.hash.replace(/#/g,'');
	
	$.ajax({
	  type: 'GET',
	  url: 'test/'+hash,
	  success: successhandler,
	  error: errorhandler
	 });
}
function handleSaveClick() {
	var params = {
			'user1': $('[data-yarn=user1]').val(),
			'gotget': $('[name=gotget]:checked').val(),
			'gift': $('[data-yarn=gift]').val(),
			'forfrom': $('[name=forfrom]:checked').val(),
			'user2': $('[data-yarn=user2]').val()
	}
	var successhandler = function(x) {
		console.log("SUCCESS : "+x);
		x = JSON.parse(x);
		window.location.hash = x.key;
	}
	var errorhandler = function(x) {
		console.log("FAILURE: "+x);
	}
	var hash = window.location.hash.replace(/#/g,'');
	
	$.ajax({
	  type: 'POST',
	  url: 'test/'+hash,
	  data: params,
	  success: successhandler,
	  error: errorhandler
	 });
}