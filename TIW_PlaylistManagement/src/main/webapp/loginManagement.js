/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
  
    var form = e.target.closest("form");
    
    //checkValidity controlla che i campi della form siano compilati correttamente
    if (form.checkValidity()) {    
    	
    //contatto il server, che tratterà la richiesta e manderà la risposta
      makeCall("POST", 'CheckLogin', e.target.closest("form"),
        function(req) {
        	//req.readyState == la risposta è arrivata dal server in maniera asincrona!!
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200: //caso in cui tutto è andato correttamente
              	//Salva lo user in sessionStorage 
            	window.sessionStorage.setItem('username', message);
            	//Il client si occupa della selezione della prossima vista -> reindirizzo alla homePage
                window.location.href = "HomePage.html";
                break;
              case 400: // bad request
                document.getElementById("errormessage").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessage").textContent = message;
                break;
            }
          }
        }
      );
    } else { //Non invio al server dati che so già non essere validi!
    	 form.reportValidity();
    }
  });

})();