document.addEventListener("DOMContentLoaded", loadCart);

	function loadCart() {
	    fetch('/api/cart')
	        .then(response => response.json())
	        .then(cartItems => {
	            renderCartItems(cartItems);
	            updateCartBadge(cartItems); // Cập nhật số lượng trên icon giỏ hàng
	        })
	        .catch(error => console.error("Lỗi tải giỏ hàng:", error));
	}

	function renderCartItems(cartItems) {
	    const tableBody = document.getElementById("cartTableBody");
	    const totalElement = document.getElementById("cartTotal");
	    tableBody.innerHTML = "";
	    let totalPrice = 0;

	    cartItems.forEach(item => {
	        totalPrice += item.price * item.quantity;
	        const row = `
	            <tr>
	                <td>${item.productName}</td>
	                <td><img src="/uploads/${item.image}" width="100"></td>
	                <td>
	                    <input type="number" value="${item.quantity}" min="1"
	                        data-id="${item.productId}" class="update-quantity">
	                </td>
	                <td>${(item.price * item.quantity).toLocaleString()} VNĐ</td>
	                <td>
	                    <button class="remove-item btn btn-danger btn-sm" data-id="${item.productId}">Xóa</button>
	                </td>
	            </tr>
	        `;
	        tableBody.innerHTML += row;
	    });

	    totalElement.innerText = totalPrice.toLocaleString() + " VNĐ";

	    attachEventListeners(); // Gắn sự kiện sau khi render
	}

	function attachEventListeners() {
	    // Cập nhật số lượng
	    document.querySelectorAll(".update-quantity").forEach(input => {
	        input.addEventListener("change", function () {
	            const productId = this.dataset.id;
	            const quantity = this.value;
	            fetch(`/api/cart/update?productId=${productId}&quantity=${quantity}`, {
	                method: 'PUT'
	            }).then(() => loadCart());
	        });
	    });

	    // Xoá sản phẩm
	    document.querySelectorAll(".remove-item").forEach(button => {
	        button.addEventListener("click", function () {
	            const productId = this.dataset.id;
	            fetch(`/api/cart/remove/${productId}`, {
	                method: 'DELETE'
	            }).then(() => loadCart());
	        });
	    });
	}

	function updateCartBadge(cartItems) {
	    const count = cartItems.reduce((sum, item) => sum + item.quantity, 0);
	    const cartBadge = document.getElementById("cart-count");
	    if (cartBadge) {
	        cartBadge.innerText = count;
	        cartBadge.style.display = count > 0 ? 'inline-block' : 'none';
	    }
	}