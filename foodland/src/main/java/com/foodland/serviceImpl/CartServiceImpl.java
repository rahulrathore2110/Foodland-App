package com.foodland.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodland.exception.FoodCartException;
import com.foodland.exception.ItemException;
import com.foodland.exception.UserException;
import com.foodland.model.CurrentUserSession;
import com.foodland.model.FoodCart;
import com.foodland.model.Item;
import com.foodland.model.User;
import com.foodland.model.UserType;
import com.foodland.repository.FoodCartDao;
import com.foodland.repository.ItemDao;
import com.foodland.repository.SessionDao;
import com.foodland.repository.UserDao;
import com.foodland.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private FoodCartDao fdo;

	@Autowired
	private ItemDao ido;

	@Autowired
	private SessionDao sdo;

	@Autowired
	private UserDao udo;

	@Override
	public FoodCart addItemToCart(Integer itemId, String key) {

		Optional<Item> item = ido.findById(itemId);

		if (item.isPresent()) {

			CurrentUserSession cus = sdo.findByUuid(key);
			
			if(cus==null) {
				throw new UserException("enter valid key");
			}

			UserType uType = cus.getType();

			if (uType.name() == "Customer") {

				User user = udo.findByMobile(cus.getMobile());

				FoodCart cart2 = user.getCart();
				
				FoodCart cart = new FoodCart();
				
				if(cart2==null) {
					cart2 = cart;
				}
		
				cart2.getItemList().add(item.get());
				
				user.setCart(cart2);
				
				

				udo.save(user);

			return	fdo.save(cart2);

			} else {
				throw new FoodCartException("login as a customer");
			}

		} else {
			throw new ItemException("no item available with id :" + itemId);
		}

	}

	@Override
	public FoodCart increaseQuantity(Integer itemId, Integer quantity, String key) {

		Optional<Item> item = ido.findById(itemId);

		if (item.isPresent()) {

			CurrentUserSession cus = sdo.findByUuid(key);
			
			if(cus==null) {
				throw new UserException("enter valid key");
			}

			UserType uType = cus.getType();

			if (uType.name() == "Customer") {

				User user = udo.findByMobile(cus.getMobile());

				FoodCart cart = user.getCart();

				List<Item> items = cart.getItemList();
				
				Item item2 = null;
				
//				for (int i = 1; i <= quantity; i++) {
//
//					items.add(item.get());
//
//				}
//
//				udo.save(user);
//
//				return cart;
//				
				for(int i=0;i<items.size();i++) {
					if(items.get(i).getItemId()==itemId) {
						Item item3 = items.get(i);
						item3.setQuantity(item3.getQuantity()+quantity);
						ido.save(item3);
					}
				}
				
				cart.setItemList(items);
				fdo.save(cart);

				udo.save(user);

				return cart;

			} else {
				throw new FoodCartException("login as a customer");
			}

		} else {
			throw new ItemException("no item available with id :" + itemId);
		}

	}

	@Override
	public FoodCart reduceQuantity(Integer itemId, Integer quantity, String key) {

		Optional<Item> itemO = ido.findById(itemId);

		if (itemO.isPresent()) {

			Item item = itemO.get();

			CurrentUserSession cus = sdo.findByUuid(key);
			
			if(cus==null) {
				throw new UserException("enter valid key");
			}

			UserType uType = cus.getType();

			if (uType.name() == "Customer") {

				User user = udo.findByMobile(cus.getMobile());

				FoodCart cart = user.getCart();
				
				if(cart==null) {
					throw new FoodCartException("cart is empty");
				}

				List<Item> items = cart.getItemList();

//				int count = 0;
//				for (Item i : items) {
//					if (item.getItemId() == i.getItemId()) {
//						count++;
//					}
//				}
//				if (count < quantity) {
//					throw new FoodCartException("reduce Quantity should be less than actual quantity");
//				}

//				for (int i = 1; i <= quantity; i++) {
//
//					items.remove(item);
//
//				}
				for(int i=0;i<items.size();i++) {
					if(items.get(i).getItemId()==itemId) {
						Item item3 = items.get(i);
						item3.setQuantity(item3.getQuantity()-quantity);
						if(item3.getQuantity()<0) {
							throw new FoodCartException("reduce Quantity should be less than actual quantity");
						}
						
						ido.save(item3);
				
						break;
					}
				}
				cart.setItemList(items);
				fdo.save(cart);
				
				udo.save(user);

				return cart;
	

			} else {
				throw new FoodCartException("login as a customer");
			}

		} else {
			throw new ItemException("no item available with id :" + itemId);
		}
	}

	@Override
	public FoodCart removeItem(Integer itemId, String key) {
		Optional<Item> item = ido.findById(itemId);

		if (item.isPresent()) {

			CurrentUserSession cus = sdo.findByUuid(key);
			
			if(cus==null) {
				throw new UserException("enter valid key");
			}

			UserType uType = cus.getType();

			if (uType.name() == "Customer") {

				User user = udo.findByMobile(cus.getMobile());
				
				

				FoodCart cart = user.getCart();

				cart.getItemList().remove(item.get());

				udo.save(user);

				return cart;

			} else {
				throw new FoodCartException("login as a customer");
			}

		} else {
			throw new ItemException("no item available with id :" + itemId);
		}

	}

	@Override
	public String clearCart(String key) {

		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();

		if (uType.name() == "Customer") {

			User user = udo.findByMobile(cus.getMobile());

			FoodCart cart = user.getCart();
			
			if(cart==null) {
				throw new FoodCartException("cart is already empty");
			}

			if (cart.getItemList().isEmpty()) {
				throw new FoodCartException("cart is already empty");
			} else {
				cart.getItemList().clear();

				udo.save(user);

				return "your cart is now empty";
			}

		}

		throw new FoodCartException("login as a customer");

	}
	
	@Override
	public FoodCart viewCart(String key) {

		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();

		if (uType.name() == "Customer") {

			User user = udo.findByMobile(cus.getMobile());

			FoodCart cart = user.getCart();
			
			if(cart==null) {
				throw new FoodCartException("cart is empty");
			}

			if (cart.getItemList().isEmpty()) {
				throw new FoodCartException("cart is already empty");
			} else {

				return cart;
			}

		}

		throw new FoodCartException("login as a customer");

	}
}
