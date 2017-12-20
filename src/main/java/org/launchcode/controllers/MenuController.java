package org.launchcode.controllers;

import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAdd(@ModelAttribute @Valid Menu newMenu, Model model, Errors errors){

        if (errors.hasErrors()){
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }
        menuDao.save(newMenu);

        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model){

        Menu menu = menuDao.findOne(id);
        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses", menu.getCheeses());
        model.addAttribute("menuId", menu.getId());

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(@PathVariable int id, Model model){

        Menu menu = menuDao.findOne(id);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(@ModelAttribute @Valid AddMenuItemForm form, Errors errors, Model model){

        if (errors.hasErrors()){
            model.addAttribute("form", form);
            return "menu/add-item";
        }

        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(cheeseDao.findOne(form.getCheeseId()));
        menuDao.save(theMenu);

        return "redirect:view/" + theMenu.getId();
    }
}
