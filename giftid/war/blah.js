console.log("This ran");

function start() {
  console.log("We started");
  console.log("Is there a hash? : "+window.location.hash);
  if(window.location.hash.length > 0) {
    handleLoad();
  }
  $('.button').click(handleSaveClick);
    $('#tab').click(handleViewResultsClick);
    $('[data-yarn=details]').click(handleDetailClick);
}
function handleDetailClick() {
    $('#extraDataEntry').toggle();
}
function handleViewResultsClick() {
    //$('#dataEntry').toggle(400); //<-- this is what makes fancyHide go *poof*
    //KATIE!!! THIS IS WHERE YOU CAN CHANGE THE CLASS STUFF ON BUTTON CLICK THING!!!
    $('#anotherContainerBoo').toggleClass('fancyHide');
    $('#anotherContainerBoo').toggleClass('fancyShow');
    if ($('#anotherContainerBoo').hasClass('fancyHide')) {
        $('#extraDataEntry').hide();
    }
}
function newMockElement() {
    var obj = {
        'name1' : (Math.random() < .5) ? 'Rebecca' : 'Katie',
        'name2' : (Math.random() < .5) ? 'Bob' : 'Joe',
        'getgot' : (Math.random() < .5) ? 'would like to get' : 'got',
        'forfrom' : (Math.random() < .5) ? 'for' : 'from',
        'gift' : (Math.random() < .5) ? 'gold' : 'poop',
    };
    return obj;
}
function displaySavedGiftEntry(giftData) {
  var domEl = $('[data-yarn=giftEntry]').clone();
  domEl.css('display','inherit');
  domEl.attr('data-yarn', 'foozy');
  //KATIE!!!! SET THE VALUE!!!
  domEl.find('[data-yarn=user1]').text(giftData.user1);
  domEl.find('[data-yarn=user2]').text(giftData.user2);
  if (giftData.gotget == 'got') {
    domEl.find('[data-yarn=getgot]').text("got");
  } else {
    domEl.find('[data-yarn=getgot]').text("would like to get");
  }
  domEl.find('[data-yarn=forfrom]').text(giftData.forfrom);
  domEl.find('[data-yarn=gift]').text(giftData.gift);
  // END
  $('[data-yarn=listOfEntries]').append(domEl);
}
function handleLoad() {
  var successhandler = function(x) {
    console.log("SUCCESS : "+x);
    x = JSON.parse(x);
    var list = x.gifts;
    for(var j = 0; j < list.length;++j) {
      var gift = list[j];
      displaySavedGiftEntry(gift);
    }
  }
  var errorhandler = function(x) {
    console.log("FAILURE: "+x);
        console.log("We are using fake data here!");
        var list = [];
        //KATIE!!! CHANGE THE # here   //Math.random() * 10 + 1
        var num = Math.random() * 10 + 1;  // <--- handle all cases of #s (0 to 100s)
        for(var i = 0; i < num;++i) {
            list.push(newMockElement());
        }
        for(var j = 0; j < list.length;++j) {
            var gift = list[j];
            var domEl = $('[data-yarn=giftEntry]').clone();
            domEl.attr('data-yarn', 'foozy');
            //KATIE!!!! SET THE VALUE!!! number to be displayed for debugging purposes
            domEl.find('[data-yarn=user1]').text(gift.name1);
            domEl.find('[data-yarn=user2]').text(gift.name2);
            domEl.find('[data-yarn=getgot]').text(gift.getgot);
            domEl.find('[data-yarn=forform]').text(gift.forfrom);
            domEl.find('[data-yarn=gift]').text(gift.gift);
            // END
            $('[data-yarn=listOfEntries]').append(domEl);
        }
  }
  var hash = window.location.hash.replace(/#/g,'');
  
  $.ajax({
    type: 'GET',
    url: 'giftid/'+hash,
    success: successhandler,
    error: errorhandler
   });
}
function handleSaveClick() {
  var params = {
      'user1': $('[data-yarn=user1]').val(),
      'gotget': $('[name=gotget]:checked').val(),
      'gift': $('[data-yarn=gift]').val(),
      'forfrom': $('[name=forfrom]:checked').val(),
      'user2': $('[data-yarn=user2]').val()
  }
  var successhandler = function(x) {
    console.log("SUCCESS : "+x);
    x = JSON.parse(x);
    window.location.hash = x.key;
    if (x.gifts && x.gifts.length > 0) {
      displaySavedGiftEntry(x.gifts[x.gifts.length - 1]);
    }
  }
  var errorhandler = function(x) {
    console.log("FAILURE: "+x);
  }
  var hash = window.location.hash.replace(/#/g,'');
  
  $.ajax({
    type: 'POST',
    url: 'giftid/'+hash,
    data: params,
    success: successhandler,
    error: errorhandler
   });
}