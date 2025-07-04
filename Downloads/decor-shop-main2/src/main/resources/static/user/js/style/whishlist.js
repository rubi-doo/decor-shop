
       let wishlist = JSON.parse(localStorage.getItem('wishlist')) || [];

       function addToCart(id, name, imageUrl, quantity, price) {
           console.log(`Adding to cart: id=${id}, name=${name}, imageUrl=${imageUrl}, quantity=${quantity}, price=${price}`);
           fetch('/api/cart/add', {
               method: 'POST',
               headers: { 'Content-Type': 'application/json' },
               body: JSON.stringify({ productId: id, productName: name, image: imageUrl, quantity: quantity, price: price })
           })
           .then(response => { if (!response.ok) throw new Error('Add to cart failed'); return response.json(); })
           .then(data => { console.log('Success:', data); alert('Đã thêm vào giỏ hàng!'); })
           .catch(error => { console.error('Error:', error); alert('Có lỗi khi thêm vào giỏ hàng!'); });
       }

       function removeFromWishlist(id) {
           wishlist = wishlist.filter(item => item.id !== id);
           localStorage.setItem('wishlist', JSON.stringify(wishlist));
           renderWishlist();
       }

       function renderWishlist() {
           const wishlistContainer = document.querySelector('.wishlist-table tbody');
           if (wishlistContainer) {
               wishlistContainer.innerHTML = '';
               if (wishlist.length > 0) {
                   wishlist.forEach(item => {
                       const row = document.createElement('tr');
                       row.innerHTML = `
                           <td><img src="/images/${item.imageUrl}" alt="${item.name}" onerror="this.onerror=null; this.src='/images/default.png';"></td>
                           <td>${item.name}</td>
                           <td>${item.price.toLocaleString('vi-VN')} VND</td>
                           <td class="product-actions">
                               <a href="javascript:void(0)" data-id="${item.id}" data-name="${item.name}" data-image-url="${item.imageUrl}" data-price="${item.price}" class="btn-outline-primary-related btn-buy">Thêm vào giỏ <i class="fa fa-shopping-cart"></i></a>
                               <a href="/product-detail/${item.id}" class="btn-primary-related">Xem thêm</a>
                               <button class="remove-btn" data-id="${item.id}">Xóa</button>
                           </td>
                       `;
                       wishlistContainer.appendChild(row);
                   });
               } else {
                   wishlistContainer.innerHTML = '<tr><td colspan="4" class="text-center"><p>Không có sản phẩm nào trong danh sách yêu thích.</p></td></tr>';
               }
           }
       }

       document.addEventListener('DOMContentLoaded', function() {
           renderWishlist();

           document.querySelectorAll('.remove-btn').forEach(button => {
               button.addEventListener('click', function() {
                   const id = parseInt(this.getAttribute('data-id'));
                   if (id) {
                       removeFromWishlist(id);
                   }
               });
           });

           document.querySelectorAll('.btn-buy').forEach(button => {
               button.addEventListener('click', function() {
                   const id = this.getAttribute('data-id');
                   const name = this.getAttribute('data-name');
                   const imageUrl = this.getAttribute('data-image-url');
                   const price = this.getAttribute('data-price');
                   let quantity = 1;
                   if (id && name && imageUrl && price) {
                       addToCart(id, name, imageUrl, quantity, price);
                   } else {
                       console.error('Missing required data:', { id, name, imageUrl, price });
                   }
               });
           });
       });
