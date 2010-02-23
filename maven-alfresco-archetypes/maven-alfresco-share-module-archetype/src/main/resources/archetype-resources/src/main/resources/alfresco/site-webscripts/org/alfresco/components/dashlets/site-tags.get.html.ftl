<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.SiteTags("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      maxItems: ${(maxItems?string!"50")},
      activeFilter: "${preferences.siteTagsFilter!"all"}"
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet site-tags">
   <div class="title">${msg("header")}</div>
   <div class="refresh"><a id="${args.htmlid}-refresh" href="#">&nbsp;</a></div>
   <div class="toolbar flat-button">
      <input id="${args.htmlid}-all" type="checkbox" name="all" value="${msg("filter.all")}" checked="checked" />
      <input id="${args.htmlid}-filter" type="button" name="filter" value="${msg("filter.documentLibrary")}" />
      <select id="${args.htmlid}-filter-menu">
         <option value="wiki">${msg("filter.wiki")}</option>
         <option value="blog">${msg("filter.blog")}</option>                
         <option value="documentLibrary">${msg("filter.documentLibrary")}</option>
         <option value="calendar">${msg("filter.calendar")}</option>
         <option value="links">${msg("filter.links")}</option>
         <option value="discussions">${msg("filter.discussions")}</option>                
      </select>
   </div>
   <div class="body" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div id="${args.htmlid}-tags"></div>
      <div class="clear"></div>
   </div>
</div>