package com.av.services;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.av.model.Customer;
import com.av.repository.CustomerRepository;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class FileStorageService {

  @Autowired
  private GridFsTemplate gridFsTemplate;

  @Autowired
  CustomerRepository customerRepository;

  // Upload file
  public String uploadFolder(String code, MultipartFile[] files) throws IOException {
    List<String> fileNames = new java.util.ArrayList<>();
    Object objectId = null;
    GridFSFile existingFile = null;
    for (MultipartFile file : files) {
      existingFile = gridFsTemplate.findOne(new Query(Criteria.where("filename")
          .is(code + "_" + Paths.get(file.getOriginalFilename()).getFileName().toString())));
      if (existingFile == null) {
        objectId = gridFsTemplate.store(file.getInputStream(),
            code + "_" + Paths.get(file.getOriginalFilename()).getFileName().toString(),
            file.getContentType());
        fileNames.add(code + "_" + Paths.get(file.getOriginalFilename()).getFileName().toString());
      }
    }
    if (fileNames.size() > 0) {
      Customer existingCustomer = customerRepository.findByCode(code);
      if (null == existingCustomer) {
        existingCustomer = new Customer();
        existingCustomer.setCode(Integer.parseInt(code.replace("AV_", "")));
        existingCustomer.getPersonal().setFileIds(fileNames);
      } else {
        existingCustomer.getPersonal().getFileIds().addAll(fileNames);
      }
      customerRepository.save(existingCustomer);
    }
    return "File uploaded successfully";
  }

  // Upload file
  public String uploadFile(MultipartFile file) throws IOException {
    ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(),
        file.getContentType());
    // return gridFsTemplate.store(inputStream, file.getOriginalFilename(),
    // file.getContentType()).toHexString();
    return id.toHexString();
  }

  // ✅ Download file
  public GridFsResource downloadFileByName(String fileName) {
    GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(fileName)));
    if (file != null) {
      return gridFsTemplate.getResource(file);
    }
    return null;
  }

  // Download file
  public Optional<GridFsResource> downloadFile(String id) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
    GridFSFile file = gridFsTemplate.findOne(query);
    if (file != null) {
      return Optional.of(gridFsTemplate.getResource(file));
    }
    return Optional.empty();
  }
}
