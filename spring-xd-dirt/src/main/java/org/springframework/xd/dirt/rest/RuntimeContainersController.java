/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.dirt.rest;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.xd.dirt.container.store.RuntimeContainerInfoEntity;
import org.springframework.xd.dirt.container.store.RuntimeContainerInfoRepository;
import org.springframework.xd.rest.client.domain.RuntimeContainerInfoResource;


/**
 * Handles interaction with the runtime containers/and its modules.
 * 
 * @author Ilayaperumal Gopinathan
 */
@Controller
@RequestMapping("/runtime/containers")
@ExposesResourceFor(RuntimeContainerInfoResource.class)
public class RuntimeContainersController {

	private RuntimeContainerInfoRepository runtimeContainerInfoRepository;

	private ResourceAssemblerSupport<RuntimeContainerInfoEntity, RuntimeContainerInfoResource> runtimeContainerResourceAssemblerSupport;

	@Autowired
	public RuntimeContainersController(RuntimeContainerInfoRepository runtimeContainerInfoRepository) {
		this.runtimeContainerInfoRepository = runtimeContainerInfoRepository;
		runtimeContainerResourceAssemblerSupport = new RuntimeContainerInfoResourceAssembler();
	}

	/**
	 * List all the available containers
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public PagedResources<RuntimeContainerInfoResource> list(Pageable pageable,
			PagedResourcesAssembler<RuntimeContainerInfoEntity> assembler) {
		Page<RuntimeContainerInfoEntity> page = this.runtimeContainerInfoRepository.findAll(pageable);
		PagedResources<RuntimeContainerInfoResource> result = safePagedContainerResources(assembler, page);
		return result;
	}

	/*
	 * Work around https://github.com/SpringSource/spring-hateoas/issues/89
	 */
	private PagedResources<RuntimeContainerInfoResource> safePagedContainerResources(
			PagedResourcesAssembler<RuntimeContainerInfoEntity> assembler,
			Page<RuntimeContainerInfoEntity> page) {
		if (page.hasContent()) {
			return assembler.toResource(page, runtimeContainerResourceAssemblerSupport);
		}
		else {
			return new PagedResources<RuntimeContainerInfoResource>(new ArrayList<RuntimeContainerInfoResource>(), null);
		}
	}
}
