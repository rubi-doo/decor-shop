
       $(document).ready(function(){
           $('.banner_slider').owlCarousel({
               items: 1,
               loop: true,
               autoplay: true,
               autoplayTimeout: 5000,
               nav: true,
               dots: true,
               rtl: false,
               navText: ['<i class="glyphicon glyphicon-chevron-left"></i>', '<i class="glyphicon glyphicon-chevron-right"></i>']
           });

           var $carousel = $('.banner_slider');
           var $counter = $('.slider-counter');
           $carousel.on('changed.owl.carousel', function(event) {
               var current = event.item.index + 1 - event.relatedTarget._clones.length / 2;
               var total = event.item.count;
               $counter.text(current + ' / ' + total);
           });
       });
