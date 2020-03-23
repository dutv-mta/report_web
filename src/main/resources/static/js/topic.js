$(document).ready(function(){
    init_TagsInput();
})

function init_TagsInput() {
	if(typeof $.fn.tagsInput !== 'undefined'){
		$('#tags-must-word').tagsInput({
			width: 'auto'
		});

		$('#tags-stop-word').tagsInput({
			width: 'auto'
		});
	};
};