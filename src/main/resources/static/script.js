function checkDate() {
   var selectedText = document.getElementById('datepicker').value;
   var selectedDate = new Date(selectedText);
   var now = new Date();
   if (selectedDate < now) {
    alert("Zeit muss in der Zukuft liegen");
     document.getElementById('datepicker').value= " " ;
   }
 }


function required()
{

var empt = document.forms["form1"]["name"].value;
if (empt == "")
{
alert("Please input a Value");
e.preventDefault();
return false;
}
var empt2 = document.forms["form1"]["ort"].value;
if (empt2 == "")
{
alert("Please input a Value");
e.preventDefault();
return false;
}
var empt2 = document.forms["form1"]["beschreibung"].value;
if (empt2 == "")
{
alert("Please input a Value");
e.preventDefault();
return false;
}
else{
return true;
}
}

