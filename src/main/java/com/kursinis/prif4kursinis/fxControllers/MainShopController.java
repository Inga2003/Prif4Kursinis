package com.kursinis.prif4kursinis.fxControllers;

import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.time.LocalDate;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.control.TableColumn.CellEditEvent;
import org.mindrot.jbcrypt.BCrypt;

public class MainShopController implements Initializable {
    @FXML
    public ListView<Product> productList;
    @FXML
    public ListView<Product> currentOrder;
    @FXML
    public Tab usersTab;
    @FXML
    public Tab warehouseTab;
    @FXML
    public ListView<Warehouse> warehouseList;
    @FXML
    public TextField addressWarehouseField;
    @FXML
    public TextField titleWarehouseField;
    @FXML
    public Tab ordersTab;
    @FXML
    public Tab productsTab;
    @FXML
    public TableView<CustomerTableData> customerTable;
    @FXML
    public TableView<ManagerTableData> managerTable;
    @FXML
    public TabPane tabPane;
    @FXML
    public Tab primaryTab;
    @FXML
    public ListView<Product> productListManager;
    @FXML
    public TextField productTitleField;
    @FXML
    public TextArea productDescriptionField;
    @FXML
    public ComboBox<ProductType> productType;
    @FXML
    public ComboBox<Warehouse> warehouseComboBox;
    @FXML
    public DatePicker foodDateField;
    @FXML
    public TextField instructionField;
    @FXML
    public TextArea colorField;
    @FXML
    public TextField productManufacturerField;
    public TextField commentTitleField;
    public TextArea commentBodyField;
    public ListView<Comment> commentListField;
    public Tab commentTab;

    @FXML
    public TableColumn LoginColumn;
    @FXML
    public TableColumn PasswordColumn;
    @FXML
    public TableColumn AddressColumn;
    @FXML
    public TableColumn BirthDateColumn;
    @FXML
    public TableColumn CardNoColumn;
    @FXML
    public TableColumn nameColumn;
    @FXML
    public TableColumn surnameColumn;
    public ListView<Order> ordersList;
    public ListView<Product> orderItemsList;
    public TextArea productNameField;
    public Button addCommentButton;
    public Button updateCommentButton;
    public Button deleteCommentButton;
    public TableColumn mNameCol;
    public TableColumn mSurnameCol;
    public TableColumn mLoginCol;
    public TableColumn mPasswordCol;
    public TableColumn employeeIdCol;
    public TableColumn certificateCol;
    public TableColumn employmentDateCol;
    public TableColumn isAdminCol;
    public TableColumn warehouseCol;
    @FXML
    public ListView allOrdersList;
    @FXML
    public TextArea orderDateField;
    @FXML
    public TextArea orderAddressField;
    @FXML
    public TextArea customerIdField;
    @FXML
    public TextArea orderStatusField;
    @FXML
    public Tab allOrdersTab;
    @FXML
    public Button assignOrderButton;
    @FXML
    public ComboBox managerComboBox;
    public Button mCancelOrderButton;
    public TextField dateFrom;
    public TextField dateTo;
    public ComboBox managersFilter;
    public ComboBox statusFilter;
    @FXML
    public Tab chartTab;
    @FXML
    public BarChart ordersBarChart;
    @FXML
    public CategoryAxis categoryAxis;
    @FXML
    public NumberAxis numberAxis;
    @FXML
    public TextField statsDateFrom;
    @FXML
    public TextField statsDateTo;
    @FXML
    public ComboBox statsManagerComboBox;
    @FXML
    public ComboBox statsStatusComboBox;
    @FXML
    public Button addWarehouseButton;
    public Button updateWarehouseButton;
    @FXML
    public Button removeWarehouseButton;
    @FXML
    public TreeView<Comment> commentTreeView;
    @FXML
    public TextField manufacturerField;
    @FXML
    public TextField typeField;
    @FXML
    public TextField additionalField;
    @FXML
    public TextField descriptionField;
    @FXML
    public TextField orderStatusField2;
    @FXML
    public Button leaveCommentButton;
    @FXML
    public Button confirmOrderButton;
    @FXML
    public Button userCancelOrderButton;
    @FXML
    public RadioButton mark1;
    @FXML
    public RadioButton mark2;
    @FXML
    public RadioButton mark3;
    @FXML
    public RadioButton mark4;
    @FXML
    public RadioButton mark5;
    @FXML
    public TextField ratingField;
    @FXML
    public Button deleteCustomerButton;
    @FXML
    public Button deleteManagerButton;
    @FXML
    public ToggleGroup rating;
    @FXML
    public Button replyButton;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private Cart userCart;
    private List<Manager> managersList;

    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        limitAccess();
        loadData();
    }

    private void loadData() {
        customHib = new CustomHib(entityManagerFactory);
        productList.getItems().clear();
        productList.getItems().addAll(customHib.getAllRecords(Product.class));
        managersList = customHib.getAllRecords(Manager.class);

        if (currentUser != null && currentUser instanceof Customer customer) {
            if (customer.getCart() == null) {
                Cart newCart = new Cart(LocalDate.now());
                newCart.setUser(customer);
                customHib.create(newCart);
                customer.setCart(newCart); // Make sure to update the user's reference to the new Cart
            }
            loadCurrentOrder();
        }
    }

    private void limitAccess() {
        if (currentUser.getClass() == Manager.class) {
            Manager manager = (Manager) currentUser;
            if (!manager.isAdmin()) {
                addWarehouseButton.setVisible(false);
                removeWarehouseButton.setVisible(false);
                updateWarehouseButton.setVisible(false);
            }
            tabPane.getTabs().removeAll(primaryTab, ordersTab);
        } else if (currentUser.getClass() == Customer.class) {
            Customer customer = (Customer) currentUser;
            tabPane.getTabs().removeAll(usersTab, warehouseTab, productsTab, allOrdersTab, chartTab);
            commentTab.setDisable(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productType.getItems().addAll(ProductType.values());
    }

    public void loadTabValues() {
        if (productsTab.isSelected()) {
            loadProductListManager();
            warehouseComboBox.getItems().clear();
            List<Warehouse> record = customHib.getAllRecords(Warehouse.class);
            warehouseComboBox.getItems().addAll(customHib.getAllRecords(Warehouse.class));
        } else if (warehouseTab.isSelected()) {
            loadWarehouseList();
        } else if (primaryTab.isSelected()){
            loadProductList();
            loadCurrentOrder();
            commentTab.setDisable(true);
        } else if (usersTab.isSelected()){
            loadUsers();
            loadManagers();
        } else if (ordersTab.isSelected()){
            loadOrders();
            commentTab.setDisable(true);
        } else if (allOrdersTab.isSelected()){
            loadAllOrdersList();
        }
    }


    public void enableProductFields() {
        if (productType.getSelectionModel().getSelectedItem() == ProductType.FOOD) {
            foodDateField.setDisable(false);
            instructionField.setDisable(true);
            colorField.setDisable(true);
        } else if (productType.getSelectionModel().getSelectedItem() == ProductType.MEDICINES) {
            foodDateField.setDisable(true);
            instructionField.setDisable(false);
            colorField.setDisable(true);
        } else {
            foodDateField.setDisable(true);
            instructionField.setDisable(true);
            colorField.setDisable(false);
        }
    }

    //----------------------Product functionality-------------------------------//

    public void loadProductDescription(MouseEvent mouseEvent) {
        Product selectedProduct = productList.getSelectionModel().getSelectedItem();
        if (selectedProduct != null){
            manufacturerField.setText(selectedProduct.getManufacturer());
            typeField.setText(String.valueOf(selectedProduct.getProductType()));
            descriptionField.setText(selectedProduct.getDescription());
            if (selectedProduct instanceof Food food){
                additionalField.setText(String.valueOf(food.getExpirationDate()));
            } else if (selectedProduct instanceof Medicines medicines){
                additionalField.setText(medicines.getInstruction());
            } else if (selectedProduct instanceof Clothes){
                additionalField.setText(((Clothes) selectedProduct).getColor());
            }
        }
    }
    public boolean validateProductFields() {
        if (productType.getSelectionModel().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Product Type Missing", "Please select a product type.");
            return false;
        }

        if (productTitleField.getText().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Product Title Missing", "Please enter a product title.");
            return false;
        }

        if (productDescriptionField.getText().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Product Description Missing", "Please enter a product description.");
            return false;
        }

        if (productManufacturerField.getText().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Product Manufacturer Missing", "Please enter a product manufacturer.");
            return false;
        }

        if (warehouseComboBox.getSelectionModel().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Warehouse Missing", "Please select a warehouse.");
            return false;
        }

        if (productType.getSelectionModel().getSelectedItem() == ProductType.FOOD && foodDateField.getValue() == null) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Expiration Date Missing", "Please select an expiration date for food products.");
            return false;
        }

        if (productType.getSelectionModel().getSelectedItem() == ProductType.MEDICINES && instructionField.getText().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Instruction Missing", "Please enter product instructions for medicines.");
            return false;
        }

        if (productType.getSelectionModel().getSelectedItem() == ProductType.CLOTHES && colorField.getText().isEmpty()) {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Validation Error", "Color Missing", "Please enter a color for clothes products.");
            return false;
        }

        return true; // All fields are valid
    }

    private void loadProductListManager() {
        productListManager.getItems().clear();
        productListManager.getItems().addAll(customHib.getAllRecords(Product.class));
    }

    public void addNewProduct() {
        if (validateProductFields()) {
            if (productType.getSelectionModel().getSelectedItem() == ProductType.FOOD) {
                Warehouse selectedWarehouse = warehouseComboBox.getSelectionModel().getSelectedItem();
                Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
                customHib.create(new Food(productTitleField.getText(), productDescriptionField.getText(), productManufacturerField.getText(), warehouse, foodDateField.getValue()));
            } else if (productType.getSelectionModel().getSelectedItem() == ProductType.MEDICINES) {
                Warehouse selectedWarehouse = warehouseComboBox.getSelectionModel().getSelectedItem();
                Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
                customHib.create(new Medicines(productTitleField.getText(), productDescriptionField.getText(), productManufacturerField.getText(), warehouse, instructionField.getText()));
            } else if (productType.getSelectionModel().getSelectedItem() == ProductType.CLOTHES) {
                Warehouse selectedWarehouse = warehouseComboBox.getSelectionModel().getSelectedItem();
                Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
                customHib.create(new Clothes(productTitleField.getText(), productDescriptionField.getText(), productManufacturerField.getText(), warehouse, colorField.getText()));
            }
            loadProductListManager();
        }
    }

    public void updateProduct() {
        Product selectedProduct = productListManager.getSelectionModel().getSelectedItem();
        boolean hasCart = selectedProduct.getCart() != null;
        Product productFromDb;
        if (hasCart) {
            productFromDb = customHib.getProductById(Product.class, selectedProduct.getId());
        } else {
            productFromDb = customHib.getEntityById(Product.class, selectedProduct.getId());
        }
        productFromDb.setTitle(productTitleField.getText());
        productFromDb.setDescription(productDescriptionField.getText());
        productFromDb.setManufacturer(productManufacturerField.getText());


        if (productFromDb instanceof Food) {
            Food foodProduct = (Food) productFromDb;
            foodProduct.setWarehouse(warehouseComboBox.getSelectionModel().getSelectedItem());
            foodProduct.setExpirationDate(foodDateField.getValue());
        } else if (productFromDb instanceof Medicines) {
            Medicines medicineProduct = (Medicines) productFromDb;
            medicineProduct.setWarehouse(warehouseComboBox.getSelectionModel().getSelectedItem());
            medicineProduct.setInstruction(instructionField.getText());
        } else if (productFromDb instanceof Clothes) {
            Clothes clothesProduct = (Clothes) productFromDb;
            clothesProduct.setWarehouse(warehouseComboBox.getSelectionModel().getSelectedItem());
            clothesProduct.setColor(colorField.getText());
        }

        customHib.update(productFromDb);
        loadProductListManager();
    }

    public void deleteProduct() {
        Product selectedProduct = productListManager.getSelectionModel().getSelectedItem();
        if (selectedProduct.getCart() != null){
            selectedProduct.setCart(null);
            customHib.update(selectedProduct);
        }
        customHib.deleteProduct(selectedProduct.getId());
        loadWarehouseList();
        loadProductListManager();
        if (currentUser instanceof Customer) {
            loadCurrentOrder();
        }
    }

    public void loadProductInfo() {

        Product selectedProduct = productListManager.getSelectionModel().getSelectedItem();

        productTitleField.setText(selectedProduct.getTitle());
        productDescriptionField.setText(selectedProduct.getDescription());
        productManufacturerField.setText(selectedProduct.getManufacturer());
        productManufacturerField.setVisible(true);

        if (selectedProduct instanceof Food foodProduct) {
            warehouseComboBox.setValue(foodProduct.getWarehouse());
            foodDateField.setValue(foodProduct.getExpirationDate());
            instructionField.clear();
            instructionField.clear();
            productType.setValue(ProductType.FOOD);
            productType.setDisable(true);
        } else if (selectedProduct instanceof Medicines medicineProduct) {
            warehouseComboBox.setValue(medicineProduct.getWarehouse());
            instructionField.setText(medicineProduct.getInstruction());
            foodDateField.setValue(null);
            colorField.clear();
            productType.setValue(ProductType.MEDICINES);
            productType.setDisable(true);
        } else if (selectedProduct instanceof Clothes clothesProduct) {
            warehouseComboBox.setValue(clothesProduct.getWarehouse());
            colorField.setText(clothesProduct.getColor());
            foodDateField.setValue(null);
            instructionField.clear();
            productType.setValue(ProductType.CLOTHES);
            productType.setDisable(true);
        }
    }

    @FXML
    private void clearFields(ActionEvent actionEvent){
        productDescriptionField.setText(null);
        productManufacturerField.setText(null);
        warehouseComboBox.setValue(null);
        productType.setValue(null);
        productType.setDisable(false);
        instructionField.setText(null);
        foodDateField.setValue(null);
        foodDateField.setDisable(false);
        colorField.setDisable(false);
        instructionField.setDisable(false);
        colorField.setText(null);
        productTitleField.setText(null);
        warehouseComboBox.setValue(null);
    }

    //----------------------Warehouse functionality-----------------------------//

    private void loadWarehouseList() {
        warehouseList.getItems().clear();
        warehouseList.getItems().addAll(customHib.getAllRecords(Warehouse.class));
    }

    public void addNewWarehouse() {
        customHib.create(new Warehouse(titleWarehouseField.getText(), addressWarehouseField.getText()));
        loadWarehouseList();
    }

    public void updateWarehouse() {
        Warehouse selectedWarehouse = warehouseList.getSelectionModel().getSelectedItem();
        Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
        warehouse.setTitle(titleWarehouseField.getText());
        warehouse.setAddress(addressWarehouseField.getText());
        customHib.update(warehouse);
        loadWarehouseList();
    }

    public void removeWarehouse() {
        Warehouse selectedWarehouse = warehouseList.getSelectionModel().getSelectedItem();
        Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
        customHib.deleteWarehouse(selectedWarehouse.getId());
        loadWarehouseList();
    }

    public void loadWarehouseData() {
        Warehouse selectedWarehouse = warehouseList.getSelectionModel().getSelectedItem();
        titleWarehouseField.setText(selectedWarehouse.getTitle());
        addressWarehouseField.setText(selectedWarehouse.getAddress());
    }

    //--------------Comment test section ------------------------//

    public void loadCommentList(Product selectedProduct) {
        if (selectedProduct != null) {
            commentTab.setDisable(false);
            if (selectedProduct != null) {
                commentTab.setDisable(false);
                List<Comment> comments = selectedProduct.getComments();

                if (!comments.isEmpty()) {
                    int totalRating = 0;
                    int numberOfReviews = 0;

                    for (Comment comment : comments) {
                        if (comment.getReviewRating() > 0) {
                            totalRating += comment.getReviewRating();
                            numberOfReviews++;
                        }
                    }

                    if (numberOfReviews > 0) {
                        int averageRating = totalRating / numberOfReviews;
                        if (averageRating > 0) ratingField.setText(String.valueOf(averageRating));
                    }
                }
            }
            try {
                EntityManager em = entityManagerFactory.createEntityManager();
                em.getTransaction().begin();
                Product productWithComments = em.find(Product.class, selectedProduct.getId());
                productWithComments.getComments().size();
                List<Comment> commentsForProduct = productWithComments.getComments();
                em.getTransaction().commit();
                em.close();

                Map<Integer, List<Comment>> commentsByParentId = commentsForProduct.stream()
                        .collect(Collectors.groupingBy(comment -> {
                            Comment parent = comment.getParentComment();
                            return parent != null ? parent.getId() : 0; // Using 0 for top-level comments
                        }));

                TreeItem<Comment> rootItem = new TreeItem<>(new Comment(selectedProduct.getTitle() + " Comments"));
                constructCommentTree(rootItem, commentsByParentId, 0); // Start constructing the tree from top-level comments

                commentTreeView.setRoot(rootItem);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Product Selected",
                    "Please select a product to load comments.",
                    "Please select a product before loading comments."
            );
        }
        rating.selectToggle(null);
    }

    private void constructCommentTree(TreeItem<Comment> parentItem, Map<Integer, List<Comment>> commentsByParentId, int parentId) {
        List<Comment> comments = commentsByParentId.getOrDefault(parentId, Collections.emptyList());
        for (Comment comment : comments) {
            TreeItem<Comment> commentItem = new TreeItem<>(comment);
            parentItem.getChildren().add(commentItem);
            constructCommentTree(commentItem, commentsByParentId, comment.getId()); // Recursive call for child comments
        }
    }


    public void createComment() {
        Product selectedProduct = orderItemsList.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            String commentTitle = commentTitleField.getText();
            String commentBody = commentBodyField.getText();

            if (commentTitle == null || commentBody == null){
                JavaFxCustomUtils.generateAlert(
                        Alert.AlertType.ERROR,
                        "Empty fields",
                        "Please fill all the fields",
                        "Please add comment title and description before creating a comment."
                );
            }

            else{
                Comment comment = new Comment(commentTitle, commentBody);
                if (mark1.isSelected()) comment.setReviewRating(1);
                else if (mark2.isSelected()) comment.setReviewRating(2);
                else if (mark3.isSelected()) comment.setReviewRating(3);
                else if (mark4.isSelected()) comment.setReviewRating(4);
                else if (mark5.isSelected()) comment.setReviewRating(5);
                comment.setProduct(selectedProduct);
                comment.setUser(currentUser);
                customHib.create(comment);

                JavaFxCustomUtils.generateAlert(
                        Alert.AlertType.INFORMATION,
                        "Comment Created",
                        "Comment has been successfully added for the selected product.",
                        "Your comment has been recorded. Thank you for your feedback!"
                );

                loadCommentList(selectedProduct);
            }

        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Item Selected",
                    "Please select an item from the order to leave a comment.",
                    "Please select an item in the order list before leaving a comment."
            );
        }
    }

    public void updateComment() {
        Product selectedProduct = orderItemsList.getSelectionModel().getSelectedItem();
        TreeItem<Comment> selectedTreeItem = commentTreeView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null && selectedTreeItem != null && selectedTreeItem.getValue() instanceof Comment) {
            Comment selectedComment = selectedTreeItem.getValue();
            Comment commentFromDb = customHib.getEntityById(Comment.class, selectedComment.getId());
            commentFromDb.setCommentTitle(commentTitleField.getText());
            commentFromDb.setCommentBody(commentBodyField.getText());
            customHib.update(commentFromDb);

            loadCommentList(selectedProduct);
        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Item Selected",
                    "Please select an item and a comment to update.",
                    "Please select an item and a comment from the tree view before updating."
            );
        }
    }

    public void deleteComment() {
        Product selectedProduct = orderItemsList.getSelectionModel().getSelectedItem();
        TreeItem<Comment> selectedTreeItem = commentTreeView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null && selectedTreeItem != null && selectedTreeItem.getValue() instanceof Comment) {
            Comment selectedComment = selectedTreeItem.getValue();
            customHib.deleteComment(selectedComment.getId());
            loadCommentList(selectedProduct); // Reload the TreeView with updated comments
        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Item Selected",
                    "Please select an item and a comment to delete.",
                    "Please select an item and a comment from the tree view before deleting."
            );
        }
    }

    public void loadCommentInfo() {
        TreeItem<Comment> selectedTreeItem = commentTreeView.getSelectionModel().getSelectedItem();
        if (selectedTreeItem != null && selectedTreeItem.getValue() instanceof Comment) {
            Comment selectedComment = selectedTreeItem.getValue();
            if (!selectedComment.getUser().equals(currentUser)) {
                deleteCommentButton.setDisable(true);
                updateCommentButton.setDisable(true);
            }
            else {
                deleteCommentButton.setDisable(false);
                updateCommentButton.setDisable(false);
            }
            if (currentUser instanceof Manager manager && manager.isAdmin()){
                deleteCommentButton.setDisable(false);
            }
            commentTitleField.setText(selectedComment.getCommentTitle());
            commentBodyField.setText(selectedComment.getCommentBody());
            /*int ratingValueFromDB = selectedComment.getReviewRating();
            for (Toggle toggle : rating.getToggles()) {
                RadioButton radioButton = (RadioButton) toggle;
                int radioButtonValue = Integer.parseInt(radioButton.getText()); // Assuming the text on the RadioButton represents its value
                if (radioButtonValue == ratingValueFromDB) {
                    rating.selectToggle(radioButton); // Select the RadioButton that matches the rating value
                    break;
                }
            }*/
        }
    }

    public void leaveComment() {
        Product selectedProduct;
        if (currentUser instanceof Customer){
            selectedProduct = orderItemsList.getSelectionModel().getSelectedItem();
        } else {
            selectedProduct = productListManager.getSelectionModel().getSelectedItem();
        }
        if (selectedProduct != null) {
            commentTab.setDisable(false);
            addCommentButton.setVisible(true);
            updateCommentButton.setVisible(true);
            replyButton.setVisible(true);
            deleteCommentButton.setVisible(true);
            productNameField.setText(selectedProduct.getTitle());
            tabPane.getSelectionModel().select(commentTab);
            loadCommentList(selectedProduct);
        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Item Selected",
                    "Please select an item from the order to leave a comment.",
                    "Please select an item in the order list before leaving a comment."
            );
        }
    }

    public void replyToComment(ActionEvent actionEvent) {
        Product selectedProduct;
        if (currentUser instanceof Customer){
            selectedProduct = orderItemsList.getSelectionModel().getSelectedItem();
        } else {
            selectedProduct = productListManager.getSelectionModel().getSelectedItem();
        }
        TreeItem<Comment> selectedTreeItem = commentTreeView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null && selectedTreeItem != null && selectedTreeItem.getValue() instanceof Comment) {
            Comment selectedComment = selectedTreeItem.getValue();
            String replyTitle = commentTitleField.getText();
            String replyBody = commentBodyField.getText();
            if (!replyBody.isEmpty() && !replyTitle.isEmpty()) {
                Comment reply = new Comment(replyTitle, replyBody);
                reply.setProduct(selectedProduct);
                reply.setParentComment(selectedComment);
                reply.setUser(currentUser);
                customHib.create(reply);
                loadCommentList(selectedProduct);
            }// Or use loadCommentTree() method if it's suitable for your setup
        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Item Selected",
                    "Please select an item and a comment to reply to.",
                    "Please select an item and a comment from the tree view before replying."
            );
        }

        if (mark1.isSelected() || mark2.isSelected() || mark3.isSelected() || mark4.isSelected() || mark5.isSelected()){
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No review available",
                    "Please create a new comment to leave a rating",
                    "You can leave rating only when you create a new comment, not when replying to somebody else."
            );
        }
    }

    //---------primaryTab functionality-----------
    private void loadProductList() {
        productList.getItems().clear();
        productList.getItems().addAll(customHib.getAllRecords(Product.class));

    }


    public void loadCurrentOrder() {
        if (currentOrder != null) {
            currentOrder.getItems().clear();
            Customer customer = (Customer) currentUser;
            if (customer != null && customer.getCart() != null) {
                List<Product> itemsInCart = customer.getCart().getItemsInCart();
                if (itemsInCart != null) {
                    currentOrder.getItems().addAll(itemsInCart);
                }
            }
        }
    }

    public void addToCart() {
        Customer customer = (Customer) currentUser;
        Cart cart = customer.getCart();
        Product selectedProduct = productList.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Check if the product's ID is already in the cart
            boolean isAlreadyInCart = cart.getItemsInCart().stream()
                    .anyMatch(product -> product.getId() == selectedProduct.getId());
            if (isAlreadyInCart) {
                JavaFxCustomUtils.generateAlert(Alert.AlertType.INFORMATION, "Cart INFO", "Wrong product", "Selected product already exists in your cart.");
            } else {
                selectedProduct.setCart(cart);
                cart.getItemsInCart().add(selectedProduct);
                customHib.update(selectedProduct);
                loadCurrentOrder();
            }

        }
    }
    public void removeFromCart() {
        Product selectedProduct = currentOrder.getSelectionModel().getSelectedItem();
        Customer customer = (Customer) currentUser;
        Cart cart = customer.getCart();
        if (selectedProduct != null) {
            cart.getItemsInCart().remove(selectedProduct);
            selectedProduct.setCart(null);
            //customHib.update(cart);
            customHib.update(selectedProduct);
            loadCurrentOrder();
            }
    }

    public void viewComments(ActionEvent actionEvent) {
        Product selectedProduct = productList.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            commentTab.setDisable(false);
            commentTitleField.clear();
            commentBodyField.clear();
            addCommentButton.setVisible(false);
            updateCommentButton.setVisible(false);
            deleteCommentButton.setVisible(false);
            replyButton.setVisible(false);
            productNameField.setText(selectedProduct.getTitle());
            tabPane.getSelectionModel().select(commentTab);
            loadCommentList(selectedProduct);
        } else {
            JavaFxCustomUtils.generateAlert(
                    Alert.AlertType.ERROR,
                    "No Item Selected",
                    "Please select a product to view comments.",
                    "Please select a product from the list before viewing comments."
            );
        }
    }


    //---------------ORDERS ---------------------
    public void confirmOrder(ActionEvent actionEvent) {
        Customer customer = (Customer) currentUser;
        if (currentUser != null && customer.getCart() != null) {
            Order newOrder = Order.builder()
                    .orderDate(LocalDate.now())
                    .customer(customer)
                    .cart(customer.getCart())
                    .products(customer.getCart().getItemsInCart()).
                    status(Order.OrderStatus.WAITING)
                    .build();

            customHib.create(newOrder);
            List<Product> productsInCart = customer.getCart().getItemsInCart();
            for (Product product : productsInCart) {
                product.setCart(null); // Remove the reference to the cart
                customHib.update(product);
            }
            customer.getCart().getItemsInCart().clear();
            customHib.update(customer.getCart());
            loadCurrentOrder();
            JavaFxCustomUtils.generateAlert(Alert.AlertType.INFORMATION, "Order Confirmed", "Order has been confirmed.", "Thank you for your purchase!");
        } else {
            // Handle the case where the user or cart is null
            JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Invalid user or cart", "Unable to confirm order. Please try again.");
        }
    }

    private void loadOrders() {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        List<Order> userOrders = allOrders.stream()
                .filter(order -> order.getCustomer().getId() == currentUser.getId()).collect(Collectors.toList());

        ordersList.getItems().clear();
        ordersList.getItems().addAll(userOrders);
    }

    public void loadOrderItems(MouseEvent mouseEvent) {
        Order selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            orderStatusField2.setText(String.valueOf(selectedOrder.getStatus()));
            List<Product> orderProducts = selectedOrder.getProducts();
            orderItemsList.getItems().clear();
            orderItemsList.getItems().addAll(orderProducts);
            if (selectedOrder.getStatus() == Order.OrderStatus.PROCESSING){
                confirmOrderButton.setDisable(false);
                userCancelOrderButton.setDisable(false);
            } else {
                confirmOrderButton.setDisable(true);
            }
            if (selectedOrder.getStatus() != Order.OrderStatus.COMPLETED){
                leaveCommentButton.setDisable(true);
            } else {
                userCancelOrderButton.setDisable(true);
                leaveCommentButton.setDisable(false);
            }
            if (selectedOrder.getStatus() == Order.OrderStatus.WAITING){
                userCancelOrderButton.setDisable(false);
                leaveCommentButton.setDisable(true);
            }
            if (selectedOrder.getStatus() == Order.OrderStatus.CANCELED){
                userCancelOrderButton.setDisable(true);
                leaveCommentButton.setDisable(true);
                confirmOrderButton.setDisable(true);
            }
        }
    }

    @FXML
    private void confirmOrderAsReceived(){
        Order selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder != null){
            selectedOrder.setStatus(Order.OrderStatus.COMPLETED);
            customHib.update(selectedOrder);
        }
    }

    @FXML
    private void cancelOrder(){
        Order selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder != null){
            selectedOrder.setStatus(Order.OrderStatus.CANCELED);
            customHib.update(selectedOrder);
        }
    }


    //-----------Users tab---------------
    private void loadUsers() {
        if (currentUser instanceof Manager manager && manager.isAdmin()){
            customerTable.setEditable(true);
        }
        List<Customer> customersList = customHib.getAllRecords(Customer.class);
        List<CustomerTableData> customerTableDataList = customersList.stream()
                .map(CustomerTableData::new)
                .collect(Collectors.toList());
        ObservableList<CustomerTableData> customers = FXCollections.observableArrayList(customerTableDataList);
        customerTable.setItems(customers);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        BirthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        CardNoColumn.setCellValueFactory(new PropertyValueFactory<>("cardNumber"));
        LoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        PasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        deleteCustomerButton.setDisable(true);
        deleteManagerButton.setDisable(true);

        if (currentUser instanceof Manager manager && manager.isAdmin()){
            deleteCustomerButton.setDisable(false);
            deleteManagerButton.setDisable(false);
            surnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            AddressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            CardNoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            LoginColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            PasswordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        }
    }

    public void changeCustomersSurname(CellEditEvent<CustomerTableData, String> editedCell) {
        CustomerTableData selectedCustomerData = editedCell.getRowValue();
        Customer selectedCustomer = selectedCustomerData.getCustomer();
        if (selectedCustomer != null) {
            String newSurname = editedCell.getNewValue().toString();
            selectedCustomer.setSurname(newSurname);
            customHib.update(selectedCustomer);
            loadManagers();
        }
    }

    public void changeCustomersLogin(CellEditEvent<CustomerTableData, String> editedCell) {
        CustomerTableData selectedCustomerData = editedCell.getRowValue();
        Customer selectedCustomer = selectedCustomerData.getCustomer();
        if (selectedCustomer != null) {
            String newLogin = editedCell.getNewValue().toString();
            selectedCustomer.setLogin(newLogin);
            customHib.update(selectedCustomer);
            loadManagers();
        }
    }

    public void changeCustomersPassword(CellEditEvent<CustomerTableData, String> editedCell) {
        CustomerTableData selectedCustomerData = editedCell.getRowValue();
        Customer selectedCustomer = selectedCustomerData.getCustomer();

        if (selectedCustomer != null) {
            String newPassword = editedCell.getNewValue().toString();
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            selectedCustomer.setPassword(hashedPassword);
            customHib.update(selectedCustomer);
            loadUsers();
        }
    }

    public void changeCustomersCard(CellEditEvent<CustomerTableData, String> editedCell) {
        CustomerTableData selectedCustomerData = editedCell.getRowValue();
        Customer selectedCustomer = selectedCustomerData.getCustomer();
        if (selectedCustomer != null) {
            String newCard = editedCell.getNewValue().toString();
            selectedCustomer.setCardNo(newCard);
            customHib.update(selectedCustomer);
            loadManagers();
        }
    }

    public void changeCustomersAddress(CellEditEvent<CustomerTableData, String> editedCell) {
        CustomerTableData selectedCustomerData = editedCell.getRowValue();
        Customer selectedCustomer = selectedCustomerData.getCustomer();
        if (selectedCustomer != null) {
            String newAddress = editedCell.getNewValue().toString();
            selectedCustomer.setAddress(newAddress);
            customHib.update(selectedCustomer);
            loadManagers();
        }
    }

    public void deleteSelectedCustomer() {
        CustomerTableData selectedCustomerData = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomerData != null) {
            Customer selectedCustomer = selectedCustomerData.getCustomer();
            if (selectedCustomer != null) {
                customHib.delete(Customer.class, selectedCustomer.getId()); // Assuming getId() returns the customer's ID
                loadUsers(); // Reload the TableView after deletion
            }
        }
    }

    private void loadManagers() {
        managerTable.setEditable(true);
        List<Manager> managerList = customHib.getAllRecords(Manager.class);
        List<ManagerTableData> managerTableDataList = managerList.stream()
                .map(ManagerTableData::new)
                .collect(Collectors.toList());
        ObservableList<ManagerTableData> managers = FXCollections.observableArrayList(managerTableDataList);
        managerTable.setItems(managers);
        if (currentUser instanceof Manager manager && manager.isAdmin()){
        mNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        mSurnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        mLoginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        mPasswordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        certificateCol.setCellValueFactory(new PropertyValueFactory<>("medCertificate"));
        employmentDateCol.setCellValueFactory(new PropertyValueFactory<>("employmentDate"));
        isAdminCol.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        mSurnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        mLoginCol.setCellFactory(TextFieldTableCell.forTableColumn());
        mPasswordCol.setCellFactory(TextFieldTableCell.forTableColumn());
        certificateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        }

        else if (currentUser instanceof Manager manager && !manager.isAdmin() ){
            List<ManagerTableData> currentUserData = managerTableDataList.stream()
                    .filter(data -> data.getLogin().equals(manager.getLogin())) // Assuming 'getLogin()' retrieves the login info
                    .collect(Collectors.toList());

            ObservableList<ManagerTableData> currentUserManager = FXCollections.observableArrayList(currentUserData);
            managerTable.setItems(currentUserManager);

            mNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            mSurnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
            mLoginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
            mPasswordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
            employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
            certificateCol.setCellValueFactory(new PropertyValueFactory<>("medCertificate"));
            employmentDateCol.setCellValueFactory(new PropertyValueFactory<>("employmentDate"));
            isAdminCol.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

            mSurnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            mLoginCol.setCellFactory(TextFieldTableCell.forTableColumn());
            mPasswordCol.setCellFactory(TextFieldTableCell.forTableColumn());
            certificateCol.setCellFactory(TextFieldTableCell.forTableColumn());

        }
    }

    public void deleteSelectedManager() {
        ManagerTableData selectedManagerData = managerTable.getSelectionModel().getSelectedItem();
        if (selectedManagerData != null) {
            Manager selectedManager = selectedManagerData.getManager();
            for (Order order : selectedManager.getOrders()) {
                if (order.getManager().equals(selectedManager)) {
                    order.setManager(null);
                    customHib.update(order);
                }
            }
            if (selectedManager != null) {
                customHib.delete(Manager.class, selectedManager.getId()); // Assuming getId() returns the customer's ID
                loadManagers();
            }
        }
    }

    public void changeManagersSurname(CellEditEvent<ManagerTableData, String> editedCell) {
        ManagerTableData selectedManagerData = editedCell.getRowValue();
        Manager selectedManager = selectedManagerData.getManager();
        if (selectedManager != null) {
            String newSurname = editedCell.getNewValue().toString();
            selectedManager.setSurname(newSurname);
            customHib.update(selectedManager);
            loadManagers();
        }
    }

    public void changeManagersLogin(CellEditEvent<ManagerTableData, String> editedCell) {
        ManagerTableData selectedManagerData = editedCell.getRowValue();
        Manager selectedManager = selectedManagerData.getManager();
        if (selectedManager != null) {
            String newLogin = editedCell.getNewValue().toString();
            selectedManager.setLogin(newLogin);
            customHib.update(selectedManager);
            loadManagers();
        }
    }

    public void changeManagersPassword(CellEditEvent<ManagerTableData, String> editedCell) {
        ManagerTableData selectedManagerData = editedCell.getRowValue();
        Manager selectedManager = selectedManagerData.getManager();

        if (selectedManager != null) {
            String newPassword = editedCell.getNewValue().toString();
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            selectedManager.setPassword(hashedPassword);
            customHib.update(selectedManager);
            loadManagers();
        }
    }

    public void changeManagersCertificate(CellEditEvent<ManagerTableData, String> editedCell) {
        ManagerTableData selectedManagerData = editedCell.getRowValue();
        Manager selectedManager = selectedManagerData.getManager();
        if (selectedManager != null) {
            String newMedCertificate = editedCell.getNewValue().toString();
            selectedManager.setMedCertificate(newMedCertificate);
            customHib.update(selectedManager);
            loadManagers();
        }
    }


    //----------------MANAGERS ORDERS TAB---------------------------

    @FXML
    private void loadAllOrdersList() {
        LocalDate today = LocalDate.now();
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        allOrdersList.getItems().clear();
        managerComboBox.getItems().clear();
        managersFilter.getItems().clear();
        statusFilter.getItems().clear();
        allOrders.sort(Comparator.comparing((Order order) -> order.getStatus().equals(Order.OrderStatus.WAITING) && order.getOrderDate().isBefore(today) ? 0 : 1)
                .thenComparing(Order::getOrderDate));

        allOrdersList.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);

                if (empty || order == null) {
                    setText(null);
                    setStyle(""); // Reset cell style
                } else {
                    if (order.getStatus().equals(Order.OrderStatus.WAITING) && order.getOrderDate().isBefore(today)) {
                        setText(order.toString()); // Set the text based on your order's representation

                        setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        setText(order.toString());
                        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }
            }
        });

        for (Order order : allOrders) {
            if (order.getStatus().equals(Order.OrderStatus.WAITING)) {
                allOrdersList.getItems().add(order);
            }
        }
        managerComboBox.getItems().addAll(managersList);
        managersFilter.getItems().addAll(managersList);
        statusFilter.getItems().addAll(Order.OrderStatus.values());
    }

    public void loadOrderInfo() {
        Object selectedObject = allOrdersList.getSelectionModel().getSelectedItem();
        Order selectedOrder = (Order) selectedObject;
        Customer customer = (Customer) selectedOrder.getCustomer();

        orderDateField.setText(String.valueOf(selectedOrder.getOrderDate()));
        customerIdField.setText(String.valueOf(selectedOrder.getCustomer().getId()));
        orderAddressField.setText(customer.getAddress());
        orderStatusField.setText(String.valueOf(selectedOrder.getStatus()));
        if (selectedOrder.getManager() != null)
        managerComboBox.setValue(selectedOrder.getManager());
        else managerComboBox.setValue("NOT ASSIGNED YET");

        if (selectedOrder.getStatus() == Order.OrderStatus.CANCELED || selectedOrder.getStatus() == Order.OrderStatus.COMPLETED){
            managerComboBox.setDisable(true);
            assignOrderButton.setDisable(true);
            mCancelOrderButton.setDisable(true);
        }else{
            managerComboBox.setDisable(false);
            assignOrderButton.setDisable(false);
            mCancelOrderButton.setDisable(false);
        }

        if (selectedOrder.getStatus() == Order.OrderStatus.PROCESSING || selectedOrder.getStatus() == Order.OrderStatus.COMPLETED){
            mCancelOrderButton.setDisable(true);
        }else{
            mCancelOrderButton.setDisable(false);
        }
    }

    public void assignOrder(ActionEvent actionEvent) {
        Object selectedObject = allOrdersList.getSelectionModel().getSelectedItem();
        Order selectedOrder = (Order) selectedObject;
        Manager selectedManager = (Manager) managerComboBox.getSelectionModel().getSelectedItem();
        if (selectedOrder != null && selectedManager != null){
            selectedOrder.setManager(selectedManager);
            selectedOrder.setStatus(Order.OrderStatus.PROCESSING);
            customHib.update(selectedOrder);
            loadAllOrdersList();
        }
    }

    public void mCancelOrder(ActionEvent actionEvent) {
        Object selectedObject = allOrdersList.getSelectionModel().getSelectedItem();
        Order selectedOrder = (Order) selectedObject;
        selectedOrder.setStatus(Order.OrderStatus.CANCELED);
        customHib.update(selectedOrder);
        loadOrderInfo();
    }

    public void applyFilters(ActionEvent actionEvent) {
        String startDateText = dateFrom.getText();
        String endDateText = dateTo.getText();
        LocalDate selectedStartDate = null;
        LocalDate selectedEndDate = null;

        if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
            selectedStartDate = LocalDate.parse(startDateText);
            selectedEndDate = LocalDate.parse(endDateText);
        }
        Manager selectedManager = (Manager) managersFilter.getValue();
        Order.OrderStatus selectedStatus = (Order.OrderStatus) statusFilter.getValue();
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        ObservableList<Order> filteredOrders = FXCollections.observableArrayList();

        for (Order order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            Order.OrderStatus orderStatus = order.getStatus();
            if ((selectedManager == null || isManagerMatch(order, selectedManager))
                    && (selectedStartDate == null || selectedEndDate == null || isDateInRange(orderDate, selectedStartDate, selectedEndDate))
                    && (selectedStatus == null || selectedStatus.equals(orderStatus))) {
                filteredOrders.add(order);
            }
        }
        allOrdersList.setItems(filteredOrders);
    }

    // Helper methods
    private boolean isManagerMatch(Order order, Manager selectedManager) {
        Manager assignedManager = order.getManager();
        return assignedManager != null &&
                assignedManager.getName().equals(selectedManager.getName()) &&
                assignedManager.getSurname().equals(selectedManager.getSurname());
    }

    private boolean isDateInRange(LocalDate orderDate, LocalDate startDate, LocalDate endDate) {
        return (orderDate.isEqual(startDate) || orderDate.isAfter(startDate))
                && (orderDate.isEqual(endDate) || orderDate.isBefore(endDate));
    }

    @FXML
    private void clearFilters(){
        dateFrom.setText("");
        dateTo.setText("");
        managersFilter.getSelectionModel().clearSelection();
        statusFilter.getSelectionModel().clearSelection();
        loadAllOrdersList();

    }
    //----------------------------CHART STUFF-------------------------------
    public void loadChartData() {
        statsManagerComboBox.getItems().clear();
        statsStatusComboBox.getItems().clear();
        ordersBarChart.getData().clear();
        statsManagerComboBox.getItems().addAll(managersList);
        statsStatusComboBox.getItems().addAll(Order.OrderStatus.values());
        Map<Manager, Map<Order.OrderStatus, Integer>> managerOrderStatusCounts = new HashMap<>();
        List<Order> allOrders = customHib.getAllRecords(Order.class);

        for (Order order : allOrders) {
            Manager manager = order.getManager();
            if (manager != null) {
                managerOrderStatusCounts.putIfAbsent(manager, new HashMap<>());
                Map<Order.OrderStatus, Integer> statusCounts = managerOrderStatusCounts.get(manager);
                Order.OrderStatus status = order.getStatus();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
        }


        for (Manager manager : managerOrderStatusCounts.keySet()) {
            Map<Order.OrderStatus, Integer> statusCounts = managerOrderStatusCounts.get(manager);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(manager.getName());

            for (Order.OrderStatus status : statusCounts.keySet()) {
                series.getData().add(new XYChart.Data<>(status.toString(), statusCounts.get(status)));
            }
            ordersBarChart.getData().add(series);
        }
    }

    public void applyFiltersToChart(ActionEvent actionEvent) {
        Order.OrderStatus selectedStatus = (Order.OrderStatus) statsStatusComboBox.getValue();
        Manager selectedManager = (Manager) statsManagerComboBox.getValue();
        LocalDate fromDate = null;
        String startDateText = statsDateFrom.getText();

        if (!startDateText.isEmpty()) {
            fromDate = LocalDate.parse(statsDateFrom.getText());
        }
        // Clear existing data in the chart
        ordersBarChart.getData().clear();

        if (selectedStatus != null && selectedManager != null && fromDate == null) {
            // Case 1: Selecting both manager and status
            updateChartForManagerAndStatus(selectedManager, selectedStatus);
        } else if (selectedStatus != null && selectedManager == null && fromDate == null) {
            // Case 2: Selecting only status
            updateChartForStatus(selectedStatus);
        } else if (selectedStatus == null && selectedManager != null && fromDate == null) {
            // Case 3: Selecting only manager
            updateChartForManager(selectedManager);
        }else if (fromDate != null && selectedManager == null && selectedStatus == null) {
            // Case 4: Filtering only by date
            updateChartForDate(fromDate);
        }else if (selectedStatus != null && selectedManager != null && fromDate != null) {
            // Case 5: Selecting both manager, status, and date
            updateChartForManagerStatusAndDate(selectedManager, selectedStatus, fromDate);
        }else if (selectedStatus != null && selectedManager == null && fromDate != null) {
            // Case 6: Selecting only status and date
            updateChartForStatusAndDate(selectedStatus, fromDate);
        }else if (selectedStatus == null && selectedManager != null && fromDate != null) {
            // Case 7: Selecting only manager and date
            updateChartForManagerAndDate(selectedManager, fromDate);
        }
    }


    private void updateChartForManagerAndStatus(Manager selectedManager, Order.OrderStatus selectedStatus) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Order.OrderStatus, Integer> statusCounts = new HashMap<>();

        if (selectedStatus != null && selectedManager != null) {
            ordersBarChart.getData().clear();
            int orderCount = 0;

            for (Order order : allOrders) {
                Manager manager = order.getManager();
                Order.OrderStatus status = order.getStatus();

                if (manager != null && manager.equals(selectedManager) && status.equals(selectedStatus)) {
                    orderCount++;
                }
            }
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(selectedManager.getName());
            series.getData().add(new XYChart.Data<>(selectedStatus.toString(), orderCount));

            ordersBarChart.getData().add(series);
        }
    }

    private void updateChartForStatus(Order.OrderStatus selectedStatus) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Manager, Integer> managerOrderCounts = new HashMap<>();

        for (Order order : allOrders) {
            Manager manager = order.getManager();
            Order.OrderStatus status = order.getStatus();

            if (status.equals(selectedStatus) && manager != null) {
                managerOrderCounts.put(manager, managerOrderCounts.getOrDefault(manager, 0) + 1);
            }
        }

        for (Map.Entry<Manager, Integer> entry : managerOrderCounts.entrySet()) {
            addDataToChart(entry.getKey().getName(), Collections.singletonMap(selectedStatus, entry.getValue()));
        }
    }

    private void updateChartForManager(Manager selectedManager) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Order.OrderStatus, Integer> statusCounts = new HashMap<>();

        for (Order order : allOrders) {
            Manager manager = order.getManager();
            Order.OrderStatus status = order.getStatus();

            if (manager != null && manager.equals(selectedManager)) {
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
        }
        addDataToChart(selectedManager.getName(), statusCounts);
    }

    private void addDataToChart(String seriesName, Map<Order.OrderStatus, Integer> statusCounts) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        for (Map.Entry<Order.OrderStatus, Integer> entry : statusCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        ordersBarChart.getData().add(series);
    }

    private void updateChartForDate(LocalDate fromDate) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Manager, Map<Order.OrderStatus, Integer>> managerStatusCounts = new HashMap<>();

        for (Order order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            if (orderDate != null && !orderDate.isBefore(fromDate)) {
                Manager manager = order.getManager();
                Order.OrderStatus status = order.getStatus();
                if (manager != null) {
                    managerStatusCounts.putIfAbsent(manager, new HashMap<>());
                    Map<Order.OrderStatus, Integer> statusCounts = managerStatusCounts.get(manager);
                    statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                }
            }
        }

        for (Map.Entry<Manager, Map<Order.OrderStatus, Integer>> entry : managerStatusCounts.entrySet()) {
            Manager manager = entry.getKey();
            Map<Order.OrderStatus, Integer> statusCounts = entry.getValue();
            Map<Order.OrderStatus, Integer> completeCounts = new HashMap<>();
            completeCounts.put(Order.OrderStatus.COMPLETED, statusCounts.getOrDefault(Order.OrderStatus.COMPLETED, 0));
            completeCounts.put(Order.OrderStatus.CANCELED, statusCounts.getOrDefault(Order.OrderStatus.CANCELED, 0));
            completeCounts.put(Order.OrderStatus.PROCESSING, statusCounts.getOrDefault(Order.OrderStatus.PROCESSING, 0));
            addDataToChart(manager.getName(), completeCounts);
        }
    }

    private void updateChartForManagerStatusAndDate(Manager selectedManager, Order.OrderStatus selectedStatus, LocalDate fromDate) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Order.OrderStatus, Integer> statusCounts = new HashMap<>();

        for (Order order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            Manager manager = order.getManager();
            Order.OrderStatus status = order.getStatus();

            if (orderDate != null && !orderDate.isBefore(fromDate) && manager != null && manager.equals(selectedManager) && status.equals(selectedStatus)) {
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
        }
        addDataToChart(selectedManager.getName() + " - " + selectedStatus.toString() + " from date: " + statsDateFrom.getText(), statusCounts);
    }

    private void updateChartForStatusAndDate(Order.OrderStatus selectedStatus, LocalDate fromDate) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Manager, Map<Order.OrderStatus, Integer>> managerStatusCounts = new HashMap<>();

        for (Order order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            Manager manager = order.getManager();
            Order.OrderStatus status = order.getStatus();

            if (orderDate != null && !orderDate.isBefore(fromDate) && manager != null && status.equals(selectedStatus)) {
                managerStatusCounts.putIfAbsent(manager, new HashMap<>());
                Map<Order.OrderStatus, Integer> statusCounts = managerStatusCounts.get(manager);
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
        }

        for (Map.Entry<Manager, Map<Order.OrderStatus, Integer>> entry : managerStatusCounts.entrySet()) {
            Manager manager = entry.getKey();
            Map<Order.OrderStatus, Integer> statusCounts = entry.getValue();
            addDataToChart(manager.getName(), statusCounts);
        }
    }

    private void updateChartForManagerAndDate(Manager selectedManager, LocalDate fromDate) {
        List<Order> allOrders = customHib.getAllRecords(Order.class);
        Map<Order.OrderStatus, Integer> statusCounts = new HashMap<>();

        for (Order order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            Manager manager = order.getManager();
            Order.OrderStatus status = order.getStatus();

            if (orderDate != null && !orderDate.isBefore(fromDate) && manager != null && manager.equals(selectedManager)) {
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
            }
        }
        addDataToChart(selectedManager.getName(), statusCounts);
    }

    public void clearChartFilters(ActionEvent actionEvent) {
        statsDateFrom.setText("");
        statsManagerComboBox.getSelectionModel().clearSelection();
        statsStatusComboBox.getSelectionModel().clearSelection();
        loadChartData();
    }

}