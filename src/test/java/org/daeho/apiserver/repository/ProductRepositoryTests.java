package org.daeho.apiserver.repository;

import lombok.extern.log4j.Log4j2;
import org.daeho.apiserver.domain.Product;
import org.daeho.apiserver.domain.ProductImage;
import org.daeho.apiserver.dto.PageRequestDTO;
import org.daeho.apiserver.dto.PageResponseDTO;
import org.daeho.apiserver.dto.ProductDTO;
import org.daeho.apiserver.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTests {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Test
    public void testInsert(){
        for(int i = 0; i<10; i++){
            Product product = Product.builder()
                    .pname("상품"+i)
                    .price(100*i)
                    .pdesc("상품설명"+i)
                    .build();
            // 2개의 이미지 파일 추가
            product.addImageString(UUID.randomUUID().toString()+"_"+"IMAGE1.jpg");
            product.addImageString(UUID.randomUUID().toString()+"_"+"IMAGE2.jpg");
            productRepository.save(product);
            log.info("------------------------------------");
        }

    }

    @Transactional
    @Test
    public void testRead(){

        Long pno = 1L;
        Optional<Product> result = productRepository.findById(pno);
        Product product = result.orElseThrow();

        log.info(product); // ---------------1
        log.info(product.getImageList()); // ----------------2
    }

    @Test
    public void testRead2(){
        Long pno = 1L;
        Optional<Product> result = productRepository.selectOne(pno);
        Product product = result.orElseThrow();
        log.info(product);
        log.info(product.getImageList());
    }

    @Test
    public void testRead3(){
        Long pno = 12L;
        ProductDTO productDTO = productService.get(pno);
        log.info(productDTO);
        log.info(productDTO.getUploadFileNames());
    }

    @Test
    @Commit
    @Transactional
    public void testDelete(){
        Long pno = 2L;
        productRepository.updateToDelete(pno,true);
    }

    @Test
    public void testUpdate(){
        Long pno = 10L;
        Product product = productRepository.selectOne(pno).get();

        product.changeName("10번 상품");
        product.changeDesc("10번 상품 설명입니다.");
        product.changePrice(5000);

        // 첨부파일 수정
        product.clearList();

        product.addImageString(UUID.randomUUID().toString()+"_"+"NEWIMAGE1.jpg");
        product.addImageString(UUID.randomUUID().toString()+"_"+"NEWIMAGE2.jpg");
        product.addImageString(UUID.randomUUID().toString()+"_"+"NEWIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testList(){
//        // org.springframework.data.domain 패키지
//        Pageable pageable = PageRequest.of(0,10, Sort.by("pno").descending());
//
//        Page<Object[]> result = productRepository.selectList(pageable);
//
//        // java.util
//        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        PageResponseDTO<ProductDTO> result = productService.getList(pageRequestDTO);

        result.getDtoList().forEach(dto -> log.info(dto));
    }

    @Test
    public void testRegister(){
        ProductDTO productDTO = ProductDTO.builder()
                .pname("새로운 상품")
                .pdesc("신규 추가 상품입니다.")
                .price(1000)
                .build();

        //uuid가 있어야 함
        productDTO.setUploadFileNames(
                java.util.List.of(
                        UUID.randomUUID()+"_"+"Test1.jpg",
                        UUID.randomUUID()+"_"+"Test2.jpg"
                )
        );

        productService.register(productDTO);
    }


}
