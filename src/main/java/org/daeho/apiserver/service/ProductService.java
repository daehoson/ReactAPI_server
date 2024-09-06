package org.daeho.apiserver.service;

import org.daeho.apiserver.dto.PageRequestDTO;
import org.daeho.apiserver.dto.PageResponseDTO;
import org.daeho.apiserver.dto.ProductDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductService {
    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);

    Long register(ProductDTO productDTO);

    ProductDTO get(Long pno);

    void modify(ProductDTO productDTO);

    void remove(Long pno);
}
