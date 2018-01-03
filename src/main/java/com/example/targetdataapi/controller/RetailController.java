package com.example.targetdataapi.controller;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.targetdataapi.exceptions.NotFoundException;
import com.example.targetdataapi.exceptions.ProductNameNotFoundException;
import com.example.targetdataapi.pojo.Products;
import com.example.targetdataapi.service.ProductService;

@RestController
public class RetailController {

	private static final Logger LOG = LoggerFactory.getLogger(RetailController.class);

	@Autowired
	public ProductService productService;

	/*
	 * @Description: HTTP GET method for retrieving the Product Information
	 * 
	 * @param: Id
	 * 
	 * @return: JSON Sample Response JSON is : {"id":13860428,"name":
	 * "The Big Lebowski (Blu-ray) (Widescreen)","current_price":{"value":
	 * 13.49,"currency_code":"USD"}}
	 */

	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	public ResponseEntity<Products> getProductInfo(
           @PathVariable(value = "id") String Id) {

		Products products = new Products();
		
		/** Validate the In coming request as per spec**/

		String regex = "[0-9]+";
		if (Id == null || !Id.matches(regex)) {
			LOG.error("Invalid Product Id");

			products.setErrorCode(100032);
			products.setErrorDesc("Bad Request");
			products.setErrorMessage("Look's Like Something Is Not Right:Please Send  Valid Product Id");
			return new ResponseEntity<Products>(products, HttpStatus.BAD_REQUEST);

		} else {
			try {
				
				/**Upon Successful request validate return the product related info to user **/
				
				products = productService.returnMainResponse(Id);
				
				if (products.getId() == 0 || products == null || products.getCurrent_price() == null
						|| products.getName() == null) {
				LOG.error("Id Not Found In Database");
					products.setErrorCode(100024);
					products.setErrorDesc("Not Found");
					products.setErrorMessage("Look's Like Something Is Not Right:Cannot Find Product Id In Database");
					return new ResponseEntity<Products>(products, HttpStatus.NOT_FOUND);

				}
			} catch (NotFoundException e) {
				LOG.error("Id Not Found In Database");
				products.setErrorCode(100024);
				products.setErrorDesc("Not Found");
				products.setErrorMessage("Look's Like Something Is Not Right:Cannot Find Product Id In Database");
				return new ResponseEntity<Products>(products, HttpStatus.NOT_FOUND);

			} catch (ProductNameNotFoundException e) {
				LOG.error("Product Name Not Found From Rest Remplete");
				products.setErrorCode(100024);
				products.setErrorDesc("Not Found");
				products.setErrorMessage("Look's Like Something Is Not Right:Cannot Find Product Id In External API");
				return new ResponseEntity<Products>(products, HttpStatus.NOT_FOUND);

			}
		}
		
		return new ResponseEntity<Products>(products, HttpStatus.OK);
		

	}

	/*
	 * @Description: PUT Method to update price
	 * 
	 * @param: Id,price
	 * 
	 * @return:SuccessMessage
	 */

	@RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> productPricing(@PathVariable(value = "id") String Id,
			@RequestBody Map<String, Object> product) {
		
		/** Validate the incoming request befor updating **/

		String responseMsg = "";
		boolean isValidRequest = false;
		String regex = "[0-9]+";

		if (Id == null || !Id.matches(regex) || product == null) {
			LOG.error("Invaid Product Id!");
			isValidRequest = false;
		} else {
			
			/**Upon Valid Request Update the product pricing**/

			responseMsg = responseMsg + productService.updateProductPricing(product);
			if(responseMsg.contains("Update"))
				isValidRequest=true;
			

		}
		if (!isValidRequest) {
			responseMsg = "Update Failed";
			return new ResponseEntity<String>(responseMsg, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(responseMsg, HttpStatus.OK);
	}
	@ExceptionHandler(value = { ConstraintViolationException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleResourceNotFoundException(ConstraintViolationException e) {
         Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
         StringBuilder strBuilder = new StringBuilder();
         for (ConstraintViolation<?> violation : violations ) {
              strBuilder.append(violation.getMessage() + "\n");
         }
         return strBuilder.toString();
    }

}
