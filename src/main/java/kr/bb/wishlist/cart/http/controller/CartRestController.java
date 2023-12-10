package kr.bb.wishlist.cart.http.controller;

import bloomingblooms.response.CommonResponse;
import javax.validation.Valid;
import kr.bb.wishlist.cart.dto.CartItemProductIdDto;
import kr.bb.wishlist.cart.dto.command.AddCartItemCommand;
import kr.bb.wishlist.cart.dto.command.DeleteCartItemListCommand;
import kr.bb.wishlist.cart.dto.command.UpdateCartItemCommand;
import kr.bb.wishlist.cart.dto.response.GetUserCartItemsResponse;
import kr.bb.wishlist.cart.http.controller.message.CartItemStockMessageRequest;
import kr.bb.wishlist.cart.http.message.GetCartItemProductInfoMessageRequest;
import kr.bb.wishlist.cart.service.CartService;
import kr.bb.wishlist.cart.valueobject.AddCartItemStatus;
import kr.bb.wishlist.common.valueobject.ProductId;
import kr.bb.wishlist.common.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CartRestController {

  private final CartItemStockMessageRequest<ProductId> cartItemStockRequest;
  private final CartService<UserId, ProductId> cartService;
  private final GetCartItemProductInfoMessageRequest productInfoRequest;


  @GetMapping("/carts")
  public CommonResponse<GetUserCartItemsResponse> getUserCartProducts(
      @RequestHeader Long userId) {
    CartItemProductIdDto productIdList = cartService.getCartItem(new UserId(userId));
    return CommonResponse.success(productInfoRequest.request(productIdList));
  }

  @PostMapping("/carts")
  public CommonResponse<AddCartItemStatus> addCartItem(
      @RequestHeader Long userId, @Valid @RequestBody AddCartItemCommand<ProductId> command) {
    return CommonResponse.success( cartService.addCartItem(new UserId(userId), command.getProductId(),
        command.getSelectedQuantity()));
  }


  @PutMapping("/carts/products")
  public CommonResponse<String> deleteCartItmes(
      @RequestHeader Long userId, @Valid @RequestBody DeleteCartItemListCommand command) {
    cartService.deleteCartItems(new UserId(userId), command.getProductIdList());
    return CommonResponse.success("장바구니 상품 삭제 성공");
  }

  @PutMapping("/carts/products/{productId}")
  public CommonResponse<String> updateCartItemSelectedQuantity(
      @RequestHeader Long userId, @Valid @RequestBody UpdateCartItemCommand command,
      @PathVariable Long productId) {
    int stock = cartItemStockRequest.request(new ProductId(productId));
    cartService.updateCartItemSelectedQuantity(new UserId(userId), new ProductId(productId),
        stock + command.getUpdatedQuantity(), stock);
    return CommonResponse.success("카트 재고 업데이트 성공");

  }

}