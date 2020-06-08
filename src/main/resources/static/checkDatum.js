function checkDate() {
   var selectedText = document.getElementById('datepicker').value;
   var selectedDate = new Date(selectedText);
   var now = new Date();
   if (selectedDate < now) {
    alert("Zeit muss in der Zukuft liegen");
     document.getElementById('datepicker').value= "" ;
   }
 }