document.addEventListener("DOMContentLoaded", function() {
     fetch('/api/cart')
         .then(res => res.json())
         .then(cartItems => {
             let count = cartItems.reduce((sum, item) => sum + item.quantity, 0);
             const cartBadge = document.getElementById("cart-count");
             if (cartBadge) {
                 cartBadge.innerText = count;
                 cartBadge.style.display = count > 0 ? 'inline-block' : 'none';
             }
         });
 });