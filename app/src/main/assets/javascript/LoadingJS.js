var LoadingJS = function(){

	this.onResize();
	var self = this;
	$(window).resize(function(){
		self.onResize();
	});

}

var jsLoadingBar = new LoadingJS();
