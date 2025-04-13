$("#especialidade").autocomplete({
	source: function(request, response){
		$.ajax({
			method: "GET",
			url: "/especialidade/titulo",
			data: {
				termo: request.term
			},
			success: function (data){
				response(data);
			}
		});
	}
});