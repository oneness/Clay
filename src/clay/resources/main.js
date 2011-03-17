//Focus on text field on load
function onLoad(){
    $('#urlinput').focus()
}

function convert(){
    $('#file').text("");
    toggleLoading(true);
    var url = $('#urlinput').val();
    var action = $('#choice').val();
    $.get('/convert',
	  {url: url, action: action},
	  function(response){
	      toggleLoading(false);
	      $('#file').html(response);}
	 )
}

function toggleLoading(show){
    document.getElementById('loading').style.display = show ? "block" : "none"
}

