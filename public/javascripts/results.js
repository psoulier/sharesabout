$(document).ready(function() {

  /* Set focus to any ".search-input" text form. Currently, there's only
   * ever one on a page, so don't need to be to picky.
   */
  var searchInput = $('#search-input');
  var inputLen = searchInput.val().length * 2;

  $(searchInput).focus();
  searchInput[0].setSelectionRange(inputLen, inputLen);
});

