(function (S) {
    'use strict';

    var Inline,
        Discussion = S.view.Discussion,
        $ = S.$;

    Inline = Discussion.extend({

        className: [Discussion.prototype.className, 'crafter-social-inline-view', 'panel-group'].join(' '),

        render: function () {
            Discussion.prototype.render.apply(this, arguments);

            var me      = this;
            var panel   = this.$(me.cmpID('panel'));
            var toggler = this.$(this.cmpID('toggler'));

            toggler
                .click(function () {
                    if (panel.hasClass('in')) {
                        $(this).addClass('closed').removeClass('opened');
                    } else {
                        $(this).addClass('opened').removeClass('closed');
                    }
                    panel.toggleClass('in');
                })
                .addClass('opened');

            return this;
        },

        show: function () {

            this.$el.insertAfter(this.cfg.target);
            this.addAll();

            var view = this.cache('commentingView');
            view && view.editor();

            if (this.cfg.scrollIntoView) {

                var pos = this.$el.offset(),
                    $selection = $('body, html');

                $selection.animate({
                    scrollTop: pos.top - 100
                }, 400);

            }

        },

        hide: function () {

            var view = this.cache('commentingView');
            view && view.editor('destroy');

            this.$el.detach();

        }

    });

    Inline.DEFAULTS = $.extend({}, Discussion.DEFAULTS, {
        viewOptions: {
            hidden: ['inline.request']
        },
        scrollIntoView: true,
        templates: {
            /* jshint -W015 */
            main: [
                '<div class="panel panel-default">',
                '<div class="options-view-container pull-right"></div>',
                    '<div class="panel-heading">',
                        '<h4 class="panel-title">',
                            '<a data-indentifyme="toggler">',
                                'Discussion',
                            '</a>',
                        '</h4>',
                    '</div>',
                    '<div data-identifyme="panel" class="panel-collapse collapse in">',
                        '<div class="panel-body">',
                            '<div class="comments crafter-social-comment-thread"></div>',
                            '<div class="create-comment-view reply-box"></div>',
                        '</div>',
                    '</div>',
                '</div>'
            ].join('')
        }
    });

    S.define('view.Inline', Inline);

}) (crafter.social);