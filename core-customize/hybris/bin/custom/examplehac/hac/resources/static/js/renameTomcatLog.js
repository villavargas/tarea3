$(function() {
	var renameTomcatLogExecuteURL = $('#renameTomcatLogForm').attr('data-url');
	$('.buttonExecute').click(function(e) {
		$('.buttonExecute').attr('disabled', true);
		console.log("renameTomcatLogExecuteURL -> " + renameTomcatLogExecuteURL);
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
            url: renameTomcatLogExecuteURL,
            type: 'POST',
			headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=utf-8',
                'X-CSRF-TOKEN' : token
            },
            success: function (data) {
                hac.global.notify("Rename log file success!");
                $('.buttonExecute').attr('disabled', false);
            },
            error: hac.global.err
        });
	});
});