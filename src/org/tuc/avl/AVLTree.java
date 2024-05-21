package org.tuc.avl;

import org.tuc.interfaces.SearchInsert;

public class AVLTree implements SearchInsert
{

	Node Root;

    // returns the height of the node
    private int Height(Node key)
    {
        if (key == null)
           return 0;

        else
            return key.height;
    }


    // Balance computes the balance factor of the node
    private int Balance(Node key)
    {
        if (key == null)
           return 0;

        else
            return ( Height(key.right) - Height(key.left) );
    }


    // updateHeight updates the height of the node
    private void updateHeight(Node key)
    {
        int l = Height(key.left);
        int r = Height(key.right);

        key.height = Math.max(l , r) + 1;
    }

    Node rotateLeft(Node x)
    {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private Node rotateRight(Node y)
    {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // balanceTree balances the tree using rotations after an insertion or deletion
    private Node balanceTree(Node root)
    {
        updateHeight(root);

        int balance = Balance(root);

        if (balance > 1) //R
        {
            if (Balance(root.right) < 0)//RL
            {
                root.right = rotateRight(root.right);
                return rotateLeft(root);
            }

            else //RR
                return rotateLeft(root);
        }

        if (balance < -1)//L
        {
            if (Balance(root.left) > 0)//LR
            {
                root.left = rotateLeft(root.left);
                return rotateRight(root);
            }
            else//LL
                return rotateRight(root);
        }

        return root;
    }


    private Node insertNode(Node root, int key)
    {
        // Performs normal BST insertion
        if (root == null)
            return new Node(key);

        else if (key < root.value)
            root.left = insertNode(root.left, key);

        else
            root.right = insertNode(root.right, key);

        // Balances the tree after BST Insertion
        return balanceTree(root);
    }

    // Successor returns the next largest node
    private Node Successor(Node root)
    {
        if (root.left != null)
            return Successor(root.left);

        else
            return root;
    }


    private Node deleteNode(Node root, int key)
    {
        // Performs standard BST Deletion
        if (root == null)
            return root;

        else if (key < root.value)
            root.left = deleteNode(root.left, key);

        else if (key > root.value)
            root.right = deleteNode(root.right, key);

        else
        {
            if (root.right == null)
                root = root.left;

            else if (root.left == null)
                root = root.right;

            else
            {
                Node temp = Successor(root.right);
                root.value = temp.value;
                root.right = deleteNode(root.right, root.value);
            }
        }

        if (root == null)
            return root;

        else
            // Balances the tree after deletion
            return balanceTree(root);
    }

    // findNode is used to search for a particular value given the root
    private Node findNode(Node root, int key)
    {
        if (root == null || key==root.value)
            return root;

        if (key < root.value)
            return findNode(root.left, key);

        else
            return findNode(root.right, key);
    }

    // Utility function for insertion of node
    public void insert(int key)
    {
        if (findNode(Root , key) == null)
        {
            Root = insertNode(Root , key);
            //System.out.println("Insertion successful");
        }

        else {
         //   System.out.println("\nKey with the entered value already exists in the tree");
        }
    }
    
    /*public int search(int key)
    {
        if(findNode(Root, key) == null)
            return 0;
        else
            return 1;
    }*/

    // Utility function for deletion of node
    public void delete(int key)
    {
        if (findNode(Root , key) != null)
        {
            Root = deleteNode(Root , key);
            System.out.println("\nDeletion successful ");
        }

        else
            System.out.println("\nNo node with entered value found in tree");
    }

    public void InOrder(Node root)
    {
        if(root == null)
        {
            System.out.println("\nNo nodes in the tree");
            return;
        }

        if(root.left != null)
            InOrder(root.left);
        System.out.print(root.value + " ");
        if(root.right != null)
            InOrder(root.right);

    }

    public void PreOrder(Node root)
    {
        if(root == null)
        {
            System.out.println("No nodes in the tree");
            return;
        }

        System.out.print(root.value + " ");
        if(root.left != null)
            PreOrder(root.left);
        if(root.right != null)
            PreOrder(root.right);

    }

    public void PostOrder(Node key)
    {
        if(key == null)
        {
            System.out.println("No nodes in the tree");
            return;
        }


        if(key.left != null)
            PostOrder(key.left);
        if(key.right != null)
            PostOrder(key.right);
        System.out.print(key.value + " ");

    }

    public void removeAll() {
    	Root = null;
    }


    @Override
    
    //Method to search a key in AVL tree and return true if found
    public boolean searchKey(int key) {
    	//Call the helper method findNode with the root of the tree and the key to search
        return findNode(Root, key) != null;
    }
    
    //Method to find the number of levels accessed during the search for a key
    public int searchKeyLevels(int key) {
    	//Call the helper method searchLevels with the root of the tree and the key to search
    	 return searchLevels(Root, key);
    }
    
    //Helper method to find the number of levels accessed during the search o a key
    public int searchLevels(Node node, int key ) {
    	//If node = null key is not found , return 0
    	 if (node == null) {
             return 0;
         }
    	 //If key equal node's value, key is found at the current level , return 1
         if (key == node.value) {
             return 1;
         }
         //If key is lower that the node's value, search in the left subtree and add 1 to the current level
         else if (key < node.value) {
             return 1 + searchLevels(node.left, key);
         }
         //If we come here it means that the key is greater than the node's value, so we search in the right
         //subtree and add 1 to the current level
         else {
             return 1 + searchLevels(node.right, key);
         }
    }
}
