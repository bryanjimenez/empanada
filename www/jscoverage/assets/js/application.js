/* automatically generated by JSCoverage - do not edit */
try {
  if (typeof top === 'object' && top !== null && typeof top.opener === 'object' && top.opener !== null) {
    // this is a browser window that was opened from another window

    if (! top.opener._$jscoverage) {
      top.opener._$jscoverage = {};
    }
  }
}
catch (e) {}

try {
  if (typeof top === 'object' && top !== null) {
    // this is a browser window

    try {
      if (typeof top.opener === 'object' && top.opener !== null && top.opener._$jscoverage) {
        top._$jscoverage = top.opener._$jscoverage;
      }
    }
    catch (e) {}

    if (! top._$jscoverage) {
      top._$jscoverage = {};
    }
  }
}
catch (e) {}

try {
  if (typeof top === 'object' && top !== null && top._$jscoverage) {
    _$jscoverage = top._$jscoverage;
  }
}
catch (e) {}
if (typeof _$jscoverage !== 'object') {
  _$jscoverage = {};
}
if (! _$jscoverage['assets/js/application.js']) {
  _$jscoverage['assets/js/application.js'] = [];
  _$jscoverage['assets/js/application.js'][5] = 0;
  _$jscoverage['assets/js/application.js'][7] = 0;
  _$jscoverage['assets/js/application.js'][9] = 0;
  _$jscoverage['assets/js/application.js'][10] = 0;
  _$jscoverage['assets/js/application.js'][12] = 0;
  _$jscoverage['assets/js/application.js'][14] = 0;
  _$jscoverage['assets/js/application.js'][19] = 0;
  _$jscoverage['assets/js/application.js'][20] = 0;
  _$jscoverage['assets/js/application.js'][23] = 0;
  _$jscoverage['assets/js/application.js'][24] = 0;
  _$jscoverage['assets/js/application.js'][28] = 0;
  _$jscoverage['assets/js/application.js'][29] = 0;
  _$jscoverage['assets/js/application.js'][31] = 0;
  _$jscoverage['assets/js/application.js'][34] = 0;
  _$jscoverage['assets/js/application.js'][35] = 0;
  _$jscoverage['assets/js/application.js'][36] = 0;
  _$jscoverage['assets/js/application.js'][38] = 0;
  _$jscoverage['assets/js/application.js'][41] = 0;
  _$jscoverage['assets/js/application.js'][47] = 0;
  _$jscoverage['assets/js/application.js'][48] = 0;
  _$jscoverage['assets/js/application.js'][52] = 0;
  _$jscoverage['assets/js/application.js'][57] = 0;
  _$jscoverage['assets/js/application.js'][58] = 0;
  _$jscoverage['assets/js/application.js'][60] = 0;
  _$jscoverage['assets/js/application.js'][66] = 0;
  _$jscoverage['assets/js/application.js'][70] = 0;
  _$jscoverage['assets/js/application.js'][72] = 0;
  _$jscoverage['assets/js/application.js'][73] = 0;
  _$jscoverage['assets/js/application.js'][74] = 0;
  _$jscoverage['assets/js/application.js'][75] = 0;
  _$jscoverage['assets/js/application.js'][80] = 0;
}
_$jscoverage['assets/js/application.js'].source = ["<span class=\"c\">// NOTICE!! DO NOT USE ANY OF THIS JAVASCRIPT</span>","<span class=\"c\">// IT'S ALL JUST JUNK FOR OUR DOCS!</span>","<span class=\"c\">// ++++++++++++++++++++++++++++++++++++++++++</span>","","<span class=\"k\">!</span><span class=\"k\">function</span> <span class=\"k\">(</span>$<span class=\"k\">)</span> <span class=\"k\">{</span>","","  $<span class=\"k\">(</span><span class=\"k\">function</span><span class=\"k\">()</span><span class=\"k\">{</span>","","    <span class=\"k\">var</span> $window <span class=\"k\">=</span> $<span class=\"k\">(</span>window<span class=\"k\">)</span>","    <span class=\"k\">var</span> $body   <span class=\"k\">=</span> $<span class=\"k\">(</span>document<span class=\"k\">.</span>body<span class=\"k\">)</span>","","    <span class=\"k\">var</span> navHeight <span class=\"k\">=</span> $<span class=\"k\">(</span><span class=\"s\">'.navbar'</span><span class=\"k\">).</span>outerHeight<span class=\"k\">(</span><span class=\"k\">true</span><span class=\"k\">)</span> <span class=\"k\">+</span> <span class=\"s\">10</span>","","    $body<span class=\"k\">.</span>scrollspy<span class=\"k\">(</span><span class=\"k\">{</span>","      target<span class=\"k\">:</span> <span class=\"s\">'.bs-sidebar'</span><span class=\"k\">,</span>","      offset<span class=\"k\">:</span> navHeight","    <span class=\"k\">}</span><span class=\"k\">)</span>","","    $window<span class=\"k\">.</span>on<span class=\"k\">(</span><span class=\"s\">'load'</span><span class=\"k\">,</span> <span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","      $body<span class=\"k\">.</span>scrollspy<span class=\"k\">(</span><span class=\"s\">'refresh'</span><span class=\"k\">)</span>","    <span class=\"k\">}</span><span class=\"k\">)</span>","","    $<span class=\"k\">(</span><span class=\"s\">'.bs-docs-container [href=#]'</span><span class=\"k\">).</span>click<span class=\"k\">(</span><span class=\"k\">function</span> <span class=\"k\">(</span>e<span class=\"k\">)</span> <span class=\"k\">{</span>","      e<span class=\"k\">.</span>preventDefault<span class=\"k\">()</span>","    <span class=\"k\">}</span><span class=\"k\">)</span>","","    <span class=\"c\">// back to top</span>","    setTimeout<span class=\"k\">(</span><span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","      <span class=\"k\">var</span> $sideBar <span class=\"k\">=</span> $<span class=\"k\">(</span><span class=\"s\">'.bs-sidebar'</span><span class=\"k\">)</span>","","      $sideBar<span class=\"k\">.</span>affix<span class=\"k\">(</span><span class=\"k\">{</span>","        offset<span class=\"k\">:</span> <span class=\"k\">{</span>","          top<span class=\"k\">:</span> <span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","            <span class=\"k\">var</span> offsetTop      <span class=\"k\">=</span> $sideBar<span class=\"k\">.</span>offset<span class=\"k\">().</span>top","            <span class=\"k\">var</span> sideBarMargin  <span class=\"k\">=</span> parseInt<span class=\"k\">(</span>$sideBar<span class=\"k\">.</span>children<span class=\"k\">(</span><span class=\"s\">0</span><span class=\"k\">).</span>css<span class=\"k\">(</span><span class=\"s\">'margin-top'</span><span class=\"k\">),</span> <span class=\"s\">10</span><span class=\"k\">)</span>","            <span class=\"k\">var</span> navOuterHeight <span class=\"k\">=</span> $<span class=\"k\">(</span><span class=\"s\">'.bs-docs-nav'</span><span class=\"k\">).</span>height<span class=\"k\">()</span>","","            <span class=\"k\">return</span> <span class=\"k\">(</span><span class=\"k\">this</span><span class=\"k\">.</span>top <span class=\"k\">=</span> offsetTop <span class=\"k\">-</span> navOuterHeight <span class=\"k\">-</span> sideBarMargin<span class=\"k\">)</span>","          <span class=\"k\">}</span>","        <span class=\"k\">,</span> bottom<span class=\"k\">:</span> <span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","            <span class=\"k\">return</span> <span class=\"k\">(</span><span class=\"k\">this</span><span class=\"k\">.</span>bottom <span class=\"k\">=</span> $<span class=\"k\">(</span><span class=\"s\">'.bs-footer'</span><span class=\"k\">).</span>outerHeight<span class=\"k\">(</span><span class=\"k\">true</span><span class=\"k\">))</span>","          <span class=\"k\">}</span>","        <span class=\"k\">}</span>","      <span class=\"k\">}</span><span class=\"k\">)</span>","    <span class=\"k\">}</span><span class=\"k\">,</span> <span class=\"s\">100</span><span class=\"k\">)</span>","","    setTimeout<span class=\"k\">(</span><span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","      $<span class=\"k\">(</span><span class=\"s\">'.bs-top'</span><span class=\"k\">).</span>affix<span class=\"k\">()</span>","    <span class=\"k\">}</span><span class=\"k\">,</span> <span class=\"s\">100</span><span class=\"k\">)</span>","","    <span class=\"c\">// tooltip demo</span>","    $<span class=\"k\">(</span><span class=\"s\">'.tooltip-demo'</span><span class=\"k\">).</span>tooltip<span class=\"k\">(</span><span class=\"k\">{</span>","      selector<span class=\"k\">:</span> <span class=\"s\">\"[data-toggle=tooltip]\"</span><span class=\"k\">,</span>","      container<span class=\"k\">:</span> <span class=\"s\">\"body\"</span>","    <span class=\"k\">}</span><span class=\"k\">)</span>","","    $<span class=\"k\">(</span><span class=\"s\">'.tooltip-test'</span><span class=\"k\">).</span>tooltip<span class=\"k\">()</span>","    $<span class=\"k\">(</span><span class=\"s\">'.popover-test'</span><span class=\"k\">).</span>popover<span class=\"k\">()</span>","","    $<span class=\"k\">(</span><span class=\"s\">'.bs-docs-navbar'</span><span class=\"k\">).</span>tooltip<span class=\"k\">(</span><span class=\"k\">{</span>","      selector<span class=\"k\">:</span> <span class=\"s\">\"a[data-toggle=tooltip]\"</span><span class=\"k\">,</span>","      container<span class=\"k\">:</span> <span class=\"s\">\".bs-docs-navbar .nav\"</span>","    <span class=\"k\">}</span><span class=\"k\">)</span>","","    <span class=\"c\">// popover demo</span>","    $<span class=\"k\">(</span><span class=\"s\">\"[data-toggle=popover]\"</span><span class=\"k\">)</span>","      <span class=\"k\">.</span>popover<span class=\"k\">()</span>","","    <span class=\"c\">// button state demo</span>","    $<span class=\"k\">(</span><span class=\"s\">'#fat-btn'</span><span class=\"k\">)</span>","      <span class=\"k\">.</span>click<span class=\"k\">(</span><span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","        <span class=\"k\">var</span> btn <span class=\"k\">=</span> $<span class=\"k\">(</span><span class=\"k\">this</span><span class=\"k\">)</span>","        btn<span class=\"k\">.</span>button<span class=\"k\">(</span><span class=\"s\">'loading'</span><span class=\"k\">)</span>","        setTimeout<span class=\"k\">(</span><span class=\"k\">function</span> <span class=\"k\">()</span> <span class=\"k\">{</span>","          btn<span class=\"k\">.</span>button<span class=\"k\">(</span><span class=\"s\">'reset'</span><span class=\"k\">)</span>","        <span class=\"k\">}</span><span class=\"k\">,</span> <span class=\"s\">3000</span><span class=\"k\">)</span>","      <span class=\"k\">}</span><span class=\"k\">)</span>","","    <span class=\"c\">// carousel demo</span>","    $<span class=\"k\">(</span><span class=\"s\">'.bs-docs-carousel-example'</span><span class=\"k\">).</span>carousel<span class=\"k\">()</span>","<span class=\"k\">}</span><span class=\"k\">)</span>","","<span class=\"k\">}</span><span class=\"k\">(</span>window<span class=\"k\">.</span>jQuery<span class=\"k\">)</span>"];
_$jscoverage['assets/js/application.js'][5]++;
(! (function ($) {
  _$jscoverage['assets/js/application.js'][7]++;
  $((function () {
  _$jscoverage['assets/js/application.js'][9]++;
  var $window = $(window);
  _$jscoverage['assets/js/application.js'][10]++;
  var $body = $(document.body);
  _$jscoverage['assets/js/application.js'][12]++;
  var navHeight = ($(".navbar").outerHeight(true) + 10);
  _$jscoverage['assets/js/application.js'][14]++;
  $body.scrollspy({target: ".bs-sidebar", offset: navHeight});
  _$jscoverage['assets/js/application.js'][19]++;
  $window.on("load", (function () {
  _$jscoverage['assets/js/application.js'][20]++;
  $body.scrollspy("refresh");
}));
  _$jscoverage['assets/js/application.js'][23]++;
  $(".bs-docs-container [href=#]").click((function (e) {
  _$jscoverage['assets/js/application.js'][24]++;
  e.preventDefault();
}));
  _$jscoverage['assets/js/application.js'][28]++;
  setTimeout((function () {
  _$jscoverage['assets/js/application.js'][29]++;
  var $sideBar = $(".bs-sidebar");
  _$jscoverage['assets/js/application.js'][31]++;
  $sideBar.affix({offset: {top: (function () {
  _$jscoverage['assets/js/application.js'][34]++;
  var offsetTop = $sideBar.offset().top;
  _$jscoverage['assets/js/application.js'][35]++;
  var sideBarMargin = parseInt($sideBar.children(0).css("margin-top"), 10);
  _$jscoverage['assets/js/application.js'][36]++;
  var navOuterHeight = $(".bs-docs-nav").height();
  _$jscoverage['assets/js/application.js'][38]++;
  return (this.top = (offsetTop - navOuterHeight - sideBarMargin));
}), bottom: (function () {
  _$jscoverage['assets/js/application.js'][41]++;
  return (this.bottom = $(".bs-footer").outerHeight(true));
})}});
}), 100);
  _$jscoverage['assets/js/application.js'][47]++;
  setTimeout((function () {
  _$jscoverage['assets/js/application.js'][48]++;
  $(".bs-top").affix();
}), 100);
  _$jscoverage['assets/js/application.js'][52]++;
  $(".tooltip-demo").tooltip({selector: "[data-toggle=tooltip]", container: "body"});
  _$jscoverage['assets/js/application.js'][57]++;
  $(".tooltip-test").tooltip();
  _$jscoverage['assets/js/application.js'][58]++;
  $(".popover-test").popover();
  _$jscoverage['assets/js/application.js'][60]++;
  $(".bs-docs-navbar").tooltip({selector: "a[data-toggle=tooltip]", container: ".bs-docs-navbar .nav"});
  _$jscoverage['assets/js/application.js'][66]++;
  $("[data-toggle=popover]").popover();
  _$jscoverage['assets/js/application.js'][70]++;
  $("#fat-btn").click((function () {
  _$jscoverage['assets/js/application.js'][72]++;
  var btn = $(this);
  _$jscoverage['assets/js/application.js'][73]++;
  btn.button("loading");
  _$jscoverage['assets/js/application.js'][74]++;
  setTimeout((function () {
  _$jscoverage['assets/js/application.js'][75]++;
  btn.button("reset");
}), 3000);
}));
  _$jscoverage['assets/js/application.js'][80]++;
  $(".bs-docs-carousel-example").carousel();
}));
})(window.jQuery));