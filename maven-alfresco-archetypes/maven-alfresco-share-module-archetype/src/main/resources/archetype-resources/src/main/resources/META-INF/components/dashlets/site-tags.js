/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * Dashboard Site Tags component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.SiteTags
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * Preferences
    */
   var PREFERENCES_DASHLET = "org.alfresco.share.dashlet",
      PREF_SITE_TAGS_FILTER = PREFERENCES_DASHLET + ".siteTagsFilter";


   /**
    * Dashboard SiteTags constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.SiteTags} The new component instance
    * @constructor
    */
   Alfresco.dashlet.SiteTags = function SiteTags_constructor(htmlId)
   {
      return Alfresco.dashlet.SiteTags.superclass.constructor.call(this, "Alfresco.dashlet.SiteTags", htmlId);
   };

   /**
    * Extend from Alfresco.component.Base and add class implementation
    */
   YAHOO.extend(Alfresco.dashlet.SiteTags, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Max items
          * 
          * @property maxItems
          * @type integer
          * @default 50
          */
         maxItems: 50,

         /**
          * Currently active filter.
          * 
          * @property activeFilter
          * @type string
          * @default "all"
          */
         activeFilter: "all"
      },

      /**
       * Tags DOM container.
       * 
       * @property tagsContainer
       * @type object
       */
      tagsContainer: null,

      /**
       * ContainerId for tag scope query
       *
       * @property containerId
       * @type string
       * @default ""
       */
      containerId: null,

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function SiteTags_onReady()
      {
         var me = this;
         
         // The tags container
         this.tagsContainer = Dom.get(this.id + "-tags");
         
         // Hook the refresh icon click
         Event.addListener(this.id + "-refresh", "click", this.onRefresh, this, true);

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // "All" filter
         this.widgets.all = new YAHOO.widget.Button(this.id + "-all",
         {
            type: "checkbox",
            value: "all",
            checked: true
         });
         this.widgets.all.on("checkedChange", this.onAllCheckedChanged, this.widgets.all, this);

         // Dropdown filter
         this.widgets.filter = new YAHOO.widget.Button(this.id + "-filter",
         {
            type: "split",
            menu: this.id + "-filter-menu",
            lazyloadmenu: false
         });
         this.widgets.filter.on("click", this.onFilterClicked, this, true);
         // Clear the lazyLoad flag and fire init event to get menu rendered into the DOM
         var menu = this.widgets.filter.getMenu();
         menu.subscribe("click", function (p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.filter.set("label", menuItem.cfg.getProperty("text"));
               me.onFilterChanged.call(me, p_aArgs[1]);
            }
         });
         
         if (this.options.activeFilter == "all")
         {
            this.widgets.filter.value = "documentLibrary";
            this.setActiveFilter("all");
         }
         else
         {
            this.widgets.filter.value = this.options.activeFilter;

            // Loop through and find the menuItem corresponding to the default filter
            var menuItems = menu.getItems(),
               menuItem,
               i, ii;

            for (i = 0, ii = menuItems.length; i < ii; i++)
            {
               menuItem = menuItems[i];
               if (menuItem.value == this.options.activeFilter)
               {
                  menu.clickEvent.fire(
                  {
                     type: "click"
                  }, menuItem);
                  break;
               }
            }
         }
      },
      
      /**
       * Event handler for refresh click
       * @method onRefresh
       * @param e {object} Event
       */
      onRefresh: function SiteTags_onRefresh(e)
      {
         if (e)
         {
            // Stop browser's default click behaviour for the link
            Event.preventDefault(e);
         }
         this.refreshTags();
      },
      
      /**
       * Refresh tags
       * @method refreshTags
       */
      refreshTags: function SiteTags_refreshTags()
      {
         // Hide the existing content
         Dom.setStyle(this.tagsContainer, "display", "none");
         
         // Make an AJAX request to the Tag Service REST API
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + "api/tagscopes/site/" + $combine(this.options.siteId, this.containerId, "tags"),
            successCallback:
            {
               fn: this.onTagsSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onTagsFailed,
               scope: this
            },
            scope: this,
            noReloadOnAuthFailure: true
         });
      },

      /**
       * Tags retrieved successfully
       * @method onTagsSuccess
       * @param p_response {object} Response object from request
       */
      onTagsSuccess: function SiteTags_onTagsSuccess(p_response)
      {
         // Retrieve the tags list from the JSON response and trim accordingly
         var tags = p_response.json.tags.slice(0, this.options.maxItems),
            numTags = tags.length,
            totalTags = 0,
            html = "",
            i, ii;

         // Work out total number of tags for scaling
         for (i = 0, ii = tags.length; i < ii; i++)
         {
            totalTags += tags[i].count;
         }

         // Tags to show?
         if (tags.length === 0)
         {
            html = '<div class="msg">' + this.msg("message.no-tags") + '</div>';
         }
         else
         {
            // Define inline scaling functions
            var tag,
               fnTagWeighting = function tagWeighting(thisTag)
               {
                  return thisTag.count / (totalTags / numTags);
               },
               fnTagFontSize = function tagFontSize(thisTag)
               {
                  return (0.5 + fnTagWeighting(thisTag)).toFixed(2);
               },
               fnSortByTagAlphabetical = function sortByTagAlphabetical(tag1, tag2)
               {
                  if (tag1.name < tag2.name)
                     return -1;
                  
                  if (tag1.name > tag2.name)
                     return 1;
                  
                  return 0;
               };
               
            // Sort tags alphabetically - standard for tag clouds
            tags.sort(fnSortByTagAlphabetical);

            // Generate HTML mark-up for each tag
            for (i = 0, ii = tags.length; i < ii; i++)
            {
               tag = tags[i];
               html += '<div class="tag"><a href="' + this.getUriTemplate(tag) + '" class="theme-color-1" style="font-size: ' + fnTagFontSize(tag) + 'em">' + $html(tag.name) + '</a></div> ';
            }
         }
         this.tagsContainer.innerHTML = html;
         
         // Fade the new content in
         Alfresco.util.Anim.fadeIn(this.tagsContainer);
      },

      /**
       * Tags request failed
       * @method onTagsFailed
       */
      onTagsFailed: function SiteTags_onTagsFailed()
      {
         this.tagsContainer.innerHTML = this.msg("refresh-failed");
         Alfresco.util.Anim.fadeIn(this.tagsContainer);
      },
      
      /**
       * Generate Uri template based on current active filter
       * @method getUriTemplate
       * @param tag {object} Tag object literal
       */
      getUriTemplate: function SiteTags_getUriTemplate(tag)
      {
         var uri = Alfresco.constants.URL_CONTEXT + 'page/site/' + this.options.siteId;
         switch (this.options.activeFilter)
         {
            case "wiki":
               uri += '/wiki';
               break;

            case "blog":
               uri += '/blog-postlist';
               break;

            case "documentLibrary":
               uri += '/documentlibrary#filter=tag|' + encodeURIComponent(tag.name);
               break;

            case "calendar":
               uri += '/calendar';
               break;

            case "links":
               uri += '/links';
               break;

            case "discussions":
               uri += '/discussions-topiclist';
               break;
            
            default:
               uri += '/search?tag=' + encodeURIComponent(tag.name) + '&amp;a=false';
         }
         return uri;
      },

      /**
       * Sets the active filter highlight in the UI
       * @method updateFilterUI
       */
      updateFilterUI: function SiteTags_updateFilterUI()
      {
         switch (this.options.activeFilter)
         {
            case "all":
               Dom.removeClass(this.widgets.filter.get("element"), "yui-checkbox-button-checked");
               break;

            default:
               this.widgets.all.set("checked", false, true);
               Dom.addClass(this.widgets.filter.get("element"), "yui-checkbox-button-checked");
               break;
         }
      },

      /**
       * Saves active filter to user preferences
       * @method saveActiveFilter
       * @param filter {string} New filter to set
       * @param noPersist {boolean} [Optional] If set, preferences are not updated
       */
      setActiveFilter: function SiteTags_saveActiveFilter(filter, noPersist)
      {
         this.options.activeFilter = filter;
         this.containerId = filter !== "all" ? filter : "";
         this.updateFilterUI();
         this.refreshTags();
         if (noPersist !== true)
         {
            this.services.preferences.set(PREF_SITE_TAGS_FILTER, filter);
         }
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * All tasks
       * @method onAllCheckedChanged
       * @param p_oEvent {object} Button event
       * @param p_obj {object} Button
       */
      onAllCheckedChanged: function Sitetags_onAllCheckedChanged(p_oEvent, p_obj)
      {
         this.setActiveFilter("all");
         p_obj.set("checked", true, true);
      },

      /**
       * Filter button clicked event handler
       * @method onFilterClicked
       * @param p_oEvent {object} Dom event
       */
      onFilterClicked: function SiteTags_onFilterClicked(p_oEvent)
      {
         this.setActiveFilter(this.widgets.filter.value);
      },
      
      /**
       * Filter drop-down changed event handler
       * @method onFilterChanged
       * @param p_oMenuItem {object} Selected menu item
       */
      onFilterChanged: function SiteTags_onFilterChanged(p_oMenuItem)
      {
         var filter = p_oMenuItem.value;
         this.widgets.filter.value = filter;
         this.setActiveFilter(filter);
      }
   });
})();
