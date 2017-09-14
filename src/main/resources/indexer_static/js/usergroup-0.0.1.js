$(document).ready(function() {
    var created = false;
    $('#create').click(function(){
        var lookupStr = $('#usergroup').val();
        var lookupPath = $('#lookupPath').val();
        var created = true;
        if(confirm('Do you really want to create Lookup?')) {
            $.ajax({
            	type:"POST",
            	url: '/druid/coordinator/v1/lookups/' + lookupPath,
            	data: lookupStr,
            	contentType: "application/json;charset=utf-8",
            	datatype: "json",//"xml", "html", "script", "json", "jsonp", "text".
            	success:function(data){
            	  console.log(data);
            	  alert("create successfully");
            	  created = true;
            	},
            	complete: function(request, textStatus){
            	  if(created) {
            	    created = true;
            	    setTimeout(function() { location.reload(true) }, 750);
            	  }
            	},
            	error: function(data){
            	    if(data.status == 202 && data.readyState == 4){
            	        console.log(data);
            	        created = true;
            	        alert("create successfully");
            	    } else {
            	        created = false;
            	        console.log("error data:");
            	        console.log(data);
            	        alert('request failed with status: '+data.status+' please check overlord logs');
            	    }
            	}
            });
        }
    });

    $.get('/druid/coordinator/v1/lookups', function(data) {
      $('.loading').hide();
      var items = [];
      var firstId = data[0];
      for (i = 0 ; i < data.length ; i++) {
        var id = data[i];
        var item = {};
        items.push(item);
        item.lookups = id;
        item.more = '<a onclick="viewItems(\''+ id +'\');">Items</a>';
      }
      buildTable(items, $('#lookups'));
      viewItems(firstId);
    });
});


var viewItems = function(groupId) {
    $.get('/druid/coordinator/v1/lookups/'+ groupId, function(data) {
        $('.loading').hide();
        var items = [];
        for (i = 0 ; i < data.length ; i++) {
            var id = data[i];
            var item = {};
            items.push(item);
            item.user_group = id;
            item.more = '<a onclick="viewLookup(\''+ groupId +'\',\''+ id +'\');">View</a>'
             + '<a onclick="deleteLookup(\''+ groupId +'\',\''+ id +'\');">delete</a>';
        }
        buildTable(items, $('#groups'));
    });
}

var viewLookup = function(groupId, lookupId) {
    $.get('/druid/coordinator/v1/lookups/'+ groupId + '/' + lookupId, function(data) {
        var formated = JSON.stringify(data, null, 4);
        $('#usergroup').val(formated);
        $('#lookupPath').val(groupId + '/' + lookupId);
    });
}

var deleteLookup = function(groupId, lookupId) {
    if(confirm('Do you really want to Delete User Group: ' + lookupId)) {
        $.ajax({
        	type:"DELETE",
        	url: '/druid/coordinator/v1/lookups/'+ groupId + '/' + lookupId,
        	success:function(data){
        	    alert("delete successfully");
        	},
        	complete: function(request, textStatus){
        	   setTimeout(function() { location.reload(true) }, 750);
        	},
        	error: function(data){
        		alert('Kill request failed with status: '+data.status+' please check overlord logs');
        	}
         });
      }
}
