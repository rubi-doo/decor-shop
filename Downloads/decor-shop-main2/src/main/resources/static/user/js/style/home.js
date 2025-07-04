/*<script th:inline="javascript">
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

        function addToWishlist(id, name, imageUrl, price) {
            console.log(`Adding to wishlist: id=${id}`);
            const btn = document.querySelector(`.wishlist-btn[data-id="${id}"]`);
            if (!wishlist.some(item => item.id === id)) {
                wishlist.push({ id, name, imageUrl, price });
                localStorage.setItem('wishlist', JSON.stringify(wishlist));
                if (btn) btn.classList.add('active');
                alert('Đã thêm vào danh sách yêu thích!');
            } else {
                wishlist = wishlist.filter(item => item.id !== id);
                localStorage.setItem('wishlist', JSON.stringify(wishlist));
                if (btn) btn.classList.remove('active');
                alert('Đã xóa khỏi danh sách yêu thích!');
            }
            updateWishlistStatus();
        }

        function updateWishlistStatus() {
            wishlist.forEach(item => {
                const btn = document.querySelector(`.wishlist-btn[data-id="${item.id}"]`);
                if (btn) btn.classList.add('active');
            });
        }

        // JavaScript cho chatbox
        document.addEventListener('DOMContentLoaded', function() {
            const toggle = document.querySelector('.fb-chatbox-toggle');
            const chatbox = document.querySelector('.fb-chatbox');
            const closeBtn = document.querySelector('.close-btn');
            const sendBtn = document.querySelector('.fb-chatbox-footer button');
            const input = document.querySelector('.fb-chatbox-footer input');
            const chatBody = document.querySelector('.fb-chatbox-body');

            toggle.addEventListener('click', function() {
                chatbox.style.display = chatbox.style.display === 'flex' ? 'none' : 'flex';
            });

            closeBtn.addEventListener('click', function() {
                chatbox.style.display = 'none';
            });

            sendBtn.addEventListener('click', function() {
                const message = input.value.trim();
                if (message) {
                    const messageDiv = document.createElement('div');
                    messageDiv.classList.add('message', 'sent');
                    messageDiv.textContent = message;
                    chatBody.appendChild(messageDiv);
                    chatBody.scrollTop = chatBody.scrollHeight;
                    input.value = '';

                    // Giả lập tin nhắn nhận
                    setTimeout(() => {
                        const replyDiv = document.createElement('div');
                        replyDiv.classList.add('message', 'received');
                        replyDiv.textContent = 'Cảm ơn bạn! Tôi sẽ hỗ trợ ngay!';
                        chatBody.appendChild(replyDiv);
                        chatBody.scrollTop = chatBody.scrollHeight;
                    }, 1000);
                }
            });

            document.querySelectorAll('.btn-outline-primary').forEach(button => {
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

            document.querySelectorAll('.wishlist-btn').forEach(button => {
                const id = button.getAttribute('data-id');
                if (wishlist.some(item => item.id === parseInt(id))) {
                    button.classList.add('active');
                }
                button.addEventListener('click', function(e) {
                    e.preventDefault();
                    const id = this.getAttribute('data-id');
                    const name = this.getAttribute('data-name');
                    const imageUrl = this.getAttribute('data-image-url');
                    const price = this.getAttribute('data-price');
                    if (id && name && imageUrl && price) {
                        addToWishlist(parseInt(id), name, imageUrl, parseFloat(price));
                    } else {
                        console.error('Missing wishlist data:', { id, name, imageUrl, price });
                    }
                });
            });

            updateWishlistStatus();
        });
		</script>*/