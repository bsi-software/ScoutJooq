#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
${symbol_dollar}(document).ready(function() {
  var app = new scout.RemoteApp();
  app.init({
    bootstrap: {
      fonts: ['scoutIcons']
    }
  });
});
