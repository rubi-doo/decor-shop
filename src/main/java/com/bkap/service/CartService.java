package com.bkap.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.bkap.cart.CartItem;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {

    private static final String SESSION_CART = "cart"; // ten thuoc tinh cua cart (.) session

    public List<CartItem> getCart(HttpSession session) { // lay gio hang tu session
        List<CartItem> cart = (List<CartItem>) session.getAttribute(SESSION_CART); //truy xuat ds sp
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, CartItem item) {
        List<CartItem> cart = getCart(session); // lay cart hien tai
        for (CartItem cartItem : cart) {
            if (cartItem.getProductId().equals(item.getProductId())) {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity()); // cap nhat so luong
                return;
            }
        }
        cart.add(item);
    }

    public void removeFromCart(HttpSession session, Long productId) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) { // tim thay sp
            	
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public double getTotal(HttpSession session) {
        return getCart(session).stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(SESSION_CART);
    }
}
