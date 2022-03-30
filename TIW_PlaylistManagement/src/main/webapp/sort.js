// Returns the text content of a cell.
var asc = true;

(function() {

	function getCellValue(tr, idx) {
	  return tr.children[idx].textContent; // idx indexes the columns of the tr row
	}

	function resetArrows(rowHeaders){
	  for (let j = 0; j < rowHeaders.length; j++ ){
	    var toReset =   rowHeaders[j].querySelectorAll("span");
	    for (let i = 0; i < toReset.length; i++) {
	      toReset[i].className = "normalarrow";
	    }
	  }
	}

	function changeArrow(th){
	  var toChange = asc ? th.querySelector("span:first-child") : th.querySelector("span:last-child");
	  toChange.className="boldarrow";
	}
	/*
	* Creates a function that compares two rows based on the cell in the idx
	* position.
	*/
	function createComparer(idx, asc) {
	  return function(rowa, rowb) {
	    // get values to compare at column idx
	    // if order is ascending, compare 1st row to 2nd , otherwise 2nd to 1st
	    var v1 = getCellValue(asc ? rowa : rowb, idx),
	    v2 = getCellValue(asc ? rowb : rowa, idx);
	    
	    // If non numeric value
	    if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
	      return v1.toString().localeCompare(v2); // lexical comparison
	    }
	
	    // If numeric value
	    return v1 - v2; // v1 greater than v2 --> true
	  };
	}

// For all table headers f class sortable

	document.querySelectorAll('th.sortable').forEach(function(th){
		th.addEventListener("click", function () {
	
		  var table = th.closest('table');
		  var rowHeaders = table.querySelectorAll('th');
		  
		  var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
		  
		  rowsArray.sort(createComparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc));
		  
		  //  Toggle the criterion
  		  asc =  !asc;
  		  
  		  // Change arrow colors
  		  resetArrows(rowHeaders);
  		  changeArrow(th);
		  
		  // Append the sorted rows in the table body
		  for (var i = 0; i < rowsArray.length; i++) {
		    table.querySelector('tbody').appendChild(rowsArray[i]);
		  }
		  
		  });
		});
})();
