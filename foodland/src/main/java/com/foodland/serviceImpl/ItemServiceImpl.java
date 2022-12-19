package com.foodland.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodland.exception.ItemException;
import com.foodland.exception.OrderDetailException;
import com.foodland.exception.RestaurantException;
import com.foodland.exception.UserException;
import com.foodland.model.Category;
import com.foodland.model.CurrentUserSession;
import com.foodland.model.Item;
import com.foodland.model.OrderDetail;
import com.foodland.model.Restaurant;
import com.foodland.model.UserType;
import com.foodland.repository.CategoryDao;
import com.foodland.repository.ItemDao;
import com.foodland.repository.OrderDetailDao;
import com.foodland.repository.RestaurantDao;
import com.foodland.repository.SessionDao;
import com.foodland.repository.UserDao;
import com.foodland.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private UserDao udo;

	@Autowired
	private ItemDao ido;

	@Autowired
	private RestaurantDao rdo;

	@Autowired
	private SessionDao sdo;

	@Autowired
	private OrderDetailDao odo;

	@Autowired
	private CategoryDao cdo;

	@Override
	public Item addItem(Item item, String key) throws ItemException, RestaurantException {

		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();

		if (uType.name() == "Restaurent") {

			Restaurant res = rdo.findByMobile(cus.getMobile());

			List<Item> items = res.getItems();

			for (Item i : items) {

				if (i.getItemName() == item.getItemName()) {
					throw new ItemException("item already created");
				}

			}
			
			item.setRestaurant(res);

			items.add(item);
			return ido.save(item);

		} else {
			throw new RestaurantException("login as a Restaurant");
		}

	}

	@Override
	public Item updateItem(Item item, String key) throws ItemException, RestaurantException {
		CurrentUserSession cs=sdo.findByUuid(key);
		if(cs==null) {
			throw new UserException("enter valid key");
		}
		UserType uType=cs.getType();
		
		if(uType.name().equals("Restaurent")) {
			Optional<Item> oitem=ido.findById(item.getItemId());
			if(oitem.isPresent()) {
				ido.delete(item);
				return oitem.get();
			}else {
				throw new ItemException("No Item Found");
			}
		}else {
			throw new RestaurantException("Login as Restatuarnt");
		}
		
	}

	@Override
	public Item viewItem(Integer id, String key) throws ItemException, RestaurantException {
		CurrentUserSession cus=sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}
		UserType uType = cus.getType();

		if(uType.name().equals("Restaurent")||uType.name().equals("Customer")) {
			Optional<Item> item=ido.findById(id);
			if(item.isPresent()) {
				return item.get();
			}
			else {
				throw new ItemException("No Item Found with id:"+id);
			}
		}else {
			throw new RestaurantException("Log In First");
		}
		
	}

	@Override
	public Item removeItem(Integer id, String key) throws ItemException, RestaurantException {
		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();
		
		if(uType.name().equals("Restaurent")) {
			Optional<Item> oItem=ido.findById(id);
			if(oItem.isPresent())
			{
				ido.deleteAll();
				return oItem.get();
			}else {
				throw new ItemException("No Item Found "+id);
			}
			}else {
				throw new RestaurantException("Login as Restaurant");
			}
		
	}

	@Override
	public List<Item> viewAllItemBycategory(Integer id, String key) throws ItemException, RestaurantException {
		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();

		if(uType.name().equals("Restaurent")||uType.name().equals("Customer")) {
			Optional<Category> ocat=cdo.findById(id);
			List<Item> listItem=ocat.get().getItems();
			
			if(listItem.isEmpty()) {
				throw new ItemException("No item available");
			}
			return listItem;
		}else {
			throw new RestaurantException("First You need to login");
		}
	}

	@Override
	public List<Item> viewAllItemByName(String name, String key) throws ItemException, RestaurantException {
		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();
		if(uType.name().equals("Restaurent")||uType.name().equals("Customer")) {
			List<Item> items=ido.findByItemName(name);
			if(items.isEmpty()) {
				throw new ItemException("No Item availbale with name "+name);
			}
			return items;
		}else {
			throw new RestaurantException("First You need to login");
		}

	}

	@Override
	public List<Item> viewAllItemByRestaurant(Integer id, String key) throws ItemException, RestaurantException {
		CurrentUserSession cus = sdo.findByUuid(key);
		
		if(cus==null) {
			throw new UserException("enter valid key");
		}

		UserType uType = cus.getType();

		if(uType.name().equals("Restaurent")) {
				Optional<Restaurant> oRestaurant=rdo.findById(id);
				List<Item> items=oRestaurant.get().getItems();
				if(items.isEmpty()) {
					throw new ItemException("Item Not found By the restaurant "+id);
				}
				return items;
		}
		else {
			throw new RestaurantException("First you need to login");
		}
	}

}
