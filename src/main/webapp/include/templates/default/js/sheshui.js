// Search clear field value.
function OnEnter( field ) { 
  if( field.value == field.defaultValue ) { 
    field.value = ""; 
  }
}
function OnExit( field ) {
  if( field.value == "" ) { 
    field.value = field.defaultValue; 
  }
}

